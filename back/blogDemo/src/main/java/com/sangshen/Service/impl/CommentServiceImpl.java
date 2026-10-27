package com.sangshen.Service.impl;
import com.sangshen.Service.CommentService;
import com.sangshen.common.NotFoundException;
import com.sangshen.common.TooManyRequestException;
import com.sangshen.entity.Comment;
import com.sangshen.entity.Post;
import com.sangshen.entity.dto.CommentCreateRequest;
import com.sangshen.mapper.CommentMapper;
import com.sangshen.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final long MIN_INTERVAL_MS = 60_000L;    //60秒
    private static final long ONE_HOUR_MS = 3_600_000L; //一小时
    private static final int MAX_PER_HOUR = 10; //每小时最多10条
    //创建一个ConcurrentHashMap，用于存储提交记录时间
    private final Map<String, Deque<Long>> submitRecords = new ConcurrentHashMap<>();   //提交记录

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    //提交评论
    @Override
    public void applyComment(CommentCreateRequest commentCreateRequest,String ip,String userAgent) {
        checkRate(ip);
        Post post = postMapper.selectById(commentCreateRequest.getPostId());
        if(post == null || !"published".equals(post.getStatus())){
            throw new NotFoundException("当前文章不存在");
        }

        //如果60秒内重复提交，后期补上吧

        Comment comment = new Comment();
        comment.setPostId(commentCreateRequest.getPostId());
        comment.setNickname(commentCreateRequest.getNickname().trim());
        comment.setEmail(commentCreateRequest.getEmail());
        comment.setWebsite(normalizeUrl(commentCreateRequest.getWebsite()));
        comment.setContent(commentCreateRequest.getContent());
        comment.setStatus("pending");
        comment.setIp(ip);
        comment.setUserAgent(userAgent);

        commentMapper.insert(comment);
    }



    //判断网站是否合法
    private String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        String trimmed = url.trim();    //去除首位空格

        //startsWith这个方法的作用：判断字符串是否以指定前缀开始
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            throw new IllegalArgumentException("网站地址必须以 http:// 或 https:// 开头");
        }

        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("网站地址过长");
        }

        return trimmed;
    }

    //请求限流提示，根据ip地址限制提交频率
    private void checkRate(String ip){
        long now = System.currentTimeMillis();  //获取当前时间
        //这里呢是如果提交记录不存在，则创建一个空的提交记录，如果队列存在，则直接返回已有的那个
        Deque<Long> records = submitRecords.computeIfAbsent(ip, k -> new ArrayDeque<>());   //获取提交记录

        synchronized (records) {
            //record.peek拿出队列的第一个元素，也就是最早的那个时间
            while(!records.isEmpty() && now - records.peek() > ONE_HOUR_MS) {
                records.pollFirst(); // 移除过期的记录
            }

            //60秒内只能一条，这次拿出来的是队尾，也就是最新的一条信息
            if(!records.isEmpty() && now - records.peekLast() < MIN_INTERVAL_MS) {
                throw new TooManyRequestException("提交太频繁，请60秒后在尝试");
            }

            //一小时最多10条
            if(records.size() >= MAX_PER_HOUR) {
                throw new TooManyRequestException("一小时内提交次数过多，请稍后再试");
            }

            records.addLast(now);
        }
    }

}
