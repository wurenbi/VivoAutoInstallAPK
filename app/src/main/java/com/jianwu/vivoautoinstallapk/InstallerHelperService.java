package com.jianwu.vivoautoinstallapk;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstallerHelperService extends AccessibilityService {
    public static final Set<String> EVENT_PACKAGE = new HashSet<>();
    public static final Set<String> EDITTEXT_PACKAGE = new HashSet<>();
    public static final Set<String> EDITTEXT_CLASSNAME = new HashSet<>();

    static {
        EVENT_PACKAGE.add("com.vivo.secime.service");

        EDITTEXT_PACKAGE.add("com.bbk.account");

        EDITTEXT_CLASSNAME.add("android.widget.EditText");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        Log.w("InstallerHelperService", "onAccessibilityEvent rootNode is null!");
        if (rootNode == null) return;

        if (EVENT_PACKAGE.contains(event.getPackageName().toString())) {
            Log.w("InstallerHelperService", "event.getPackageName() is " + event.getPackageName());
            //vivo账号密码
            String password = (String) SharePreferencesUtils.getParam(getApplicationContext(),
                    AppConstants.KEY_PASSWORD, "");
            if (!TextUtils.isEmpty(password)) {
                fillPassword(rootNode, password);
            }
        }
        findAndClickView(rootNode);
    }

    /**
     * 自动填充密码
     */
    private void fillPassword(AccessibilityNodeInfo rootNode, String password) {
        AccessibilityNodeInfo editText = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        Log.w("InstallerHelperService", "fillPassword editText is null!");
        if (editText == null) return;

        if (EDITTEXT_PACKAGE.contains(editText.getPackageName().toString())
                && EDITTEXT_CLASSNAME.contains(editText.getClassName().toString())) {
            Log.w("InstallerHelperService",
                    "editText.getPackageName() is " + editText.getPackageName() + ","
                            + "editText.getClassName() is " + editText.getClassName());
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
    }

    /**
     * 查找按钮并点击
     */
    private void findAndClickView(AccessibilityNodeInfo rootNode) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("确定"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("继续安装"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("安装"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("打开"));

        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    @Override
    public void onInterrupt() {
    }
}
