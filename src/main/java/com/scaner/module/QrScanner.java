package com.scaner.module;

import android.Manifest;
import android.content.Context;

import com.scaner.module.helper.PermissionHelper;
import com.scaner.module.resource.SysRes;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2016/3/30.
 * @since 1.0.0
 */
public class QrScanner {

    protected static Subscriber<? super String> sSubscriber;
    /**
     * 标志：控制扫描成功后是否关闭扫描页面
     */
    protected static boolean sIsCloseAfterScanSucc;
    /**
     * 标志：控制activity栈返回时是否关闭扫描页面
     */
    protected static boolean sTag4CloseScannerWhenResume;

    /**
     * 使能：activity栈返回时关闭扫描页面
     */
    public static void enableCloseScannerWhenResume() {
        sTag4CloseScannerWhenResume = true;
    }

    /**
     * 启动二维码扫描页面，默认扫码成功后不关闭页面
     *
     * @param context 上下文
     * @return 扫码结果的观察者
     */
    public static Observable<String> start(Context context) {
        return start(context, false);
    }

    /**
     * 启动二维码扫描页面
     *
     * @param context              上下文
     * @param isCloseAfterScanSucc true 为扫描成功后关闭扫描页面，false 为不关闭，继续扫码
     * @return 扫码结果的观察者
     */
    public static Observable<String> start(final Context context, final boolean isCloseAfterScanSucc) {
        return RxPermissions.getInstance(context).request(Manifest.permission.CAMERA)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Boolean, Observable<String>>() {
                    @Override
                    public Observable<String> call(Boolean granted) {
                        if (!granted) {
                            // 2016/12/14 by woode: 完成需求《》：提示并跳到权限页面
                            String lString = context.getString(R.string
                                    .permission_help_access_camera);
                            String lText = String.format(context.getString(R.string
                                    .permission_help_tips_message), lString);
                            PermissionHelper.showWarnToast(context, lText);
                            PermissionHelper.startInstalledAppDetailsActivity(context);
                            return Observable.empty();
                        }
                        sIsCloseAfterScanSucc = isCloseAfterScanSucc;
                        QrScannerActivity.start(context);
                        return Observable.create(
                                new Observable.OnSubscribe<String>() {
                                    @Override
                                    public void call(Subscriber<? super String> subscriber) {
                                        sSubscriber = subscriber;
                                    }
                                });
                    }
                })
                ;
    }

}
