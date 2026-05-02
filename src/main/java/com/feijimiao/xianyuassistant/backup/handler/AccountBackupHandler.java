package com.feijimiao.xianyuassistant.backup.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feijimiao.xianyuassistant.backup.DataBackupHandler;
import com.feijimiao.xianyuassistant.entity.XianyuAccount;
import com.feijimiao.xianyuassistant.entity.XianyuCookie;
import com.feijimiao.xianyuassistant.mapper.XianyuAccountMapper;
import com.feijimiao.xianyuassistant.mapper.XianyuCookieMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class AccountBackupHandler implements DataBackupHandler {

    @Autowired
    private XianyuAccountMapper accountMapper;

    @Autowired
    private XianyuCookieMapper cookieMapper;

    @Override
    public String getModuleKey() {
        return "account";
    }

    @Override
    public String getModuleName() {
        return "闲鱼账号";
    }

    @Override
    public Map<String, Object> exportData() {
        List<XianyuAccount> accounts = accountMapper.selectList(null);
        List<XianyuCookie> cookies = cookieMapper.selectList(null);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accounts", accounts);
        data.put("cookies", cookies);
        return data;
    }

    @Override
    public void importData(Map<String, Object> data) {
        if (data == null) return;

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> accountMaps = (List<Map<String, Object>>) data.get("accounts");
        if (accountMaps != null) {
            for (Map<String, Object> map : accountMaps) {
                XianyuAccount account = mapToAccount(map);
                if (account == null) continue;

                LambdaQueryWrapper<XianyuAccount> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(XianyuAccount::getUnb, account.getUnb());
                XianyuAccount existing = accountMapper.selectOne(wrapper);
                if (existing == null) {
                    account.setId(null);
                    accountMapper.insert(account);
                } else {
                    account.setId(existing.getId());
                    accountMapper.updateById(account);
                }
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cookieMaps = (List<Map<String, Object>>) data.get("cookies");
        if (cookieMaps != null) {
            for (Map<String, Object> map : cookieMaps) {
                XianyuCookie cookie = mapToCookie(map);
                if (cookie == null) continue;

                LambdaQueryWrapper<XianyuCookie> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(XianyuCookie::getXianyuAccountId, cookie.getXianyuAccountId());
                XianyuCookie existing = cookieMapper.selectOne(wrapper);
                if (existing == null) {
                    cookie.setId(null);
                    cookieMapper.insert(cookie);
                } else {
                    cookie.setId(existing.getId());
                    cookieMapper.updateById(cookie);
                }
            }
        }
    }

    private XianyuAccount mapToAccount(Map<String, Object> map) {
        try {
            XianyuAccount a = new XianyuAccount();
            a.setAccountNote((String) map.get("accountNote"));
            a.setUnb((String) map.get("unb"));
            a.setDeviceId((String) map.get("deviceId"));
            a.setStatus(map.get("status") != null ? ((Number) map.get("status")).intValue() : null);
            a.setCreatedTime((String) map.get("createdTime"));
            a.setUpdatedTime((String) map.get("updatedTime"));
            return a;
        } catch (Exception e) {
            log.warn("解析账号数据失败", e);
            return null;
        }
    }

    private XianyuCookie mapToCookie(Map<String, Object> map) {
        try {
            XianyuCookie c = new XianyuCookie();
            c.setXianyuAccountId(map.get("xianyuAccountId") != null ? ((Number) map.get("xianyuAccountId")).longValue() : null);
            c.setCookieText((String) map.get("cookieText"));
            c.setMH5Tk((String) map.get("mH5Tk"));
            c.setCookieStatus(map.get("cookieStatus") != null ? ((Number) map.get("cookieStatus")).intValue() : null);
            c.setExpireTime((String) map.get("expireTime"));
            c.setWebsocketToken((String) map.get("websocketToken"));
            c.setTokenExpireTime(map.get("tokenExpireTime") != null ? ((Number) map.get("tokenExpireTime")).longValue() : null);
            c.setCreatedTime((String) map.get("createdTime"));
            c.setUpdatedTime((String) map.get("updatedTime"));
            return c;
        } catch (Exception e) {
            log.warn("解析Cookie数据失败", e);
            return null;
        }
    }
}
