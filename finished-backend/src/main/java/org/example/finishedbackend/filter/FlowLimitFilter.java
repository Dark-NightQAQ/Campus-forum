package org.example.finishedbackend.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.utils.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(-101)
public class FlowLimitFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(FlowLimitFilter.class);
    @Resource
    StringRedisTemplate template;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if (isBan(ip)) {
            chain.doFilter(request, response);
        } else {
            responseBanMessage(response);
        }
    }

    private void responseBanMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(403, "请求频率过高, 请30s后重试").asJSONString());
    }

    private boolean isBan(String ip) {
        if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BAN + ip))) return false;
        return limitPeriodCheck(ip);
    }

    private boolean limitPeriodCheck(String ip) {
        if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNTER + ip))) {
            Long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
            if (increment > 20) {
                template.opsForValue().set(Const.FLOW_LIMIT_BAN + ip, "", 30, TimeUnit.SECONDS);
                log.info("{}地址也许正在压测网站, 请注意!", ip);
                return false;
            }
        } else {
            template.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 1, TimeUnit.SECONDS);
        }
        return true;
    }

}
