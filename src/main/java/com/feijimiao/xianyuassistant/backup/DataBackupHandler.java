package com.feijimiao.xianyuassistant.backup;

import java.util.Map;

public interface DataBackupHandler {

    String getModuleKey();

    String getModuleName();

    Map<String, Object> exportData();

    void importData(Map<String, Object> data);
}
