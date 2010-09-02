package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.os.Build;

/**
 * VersionedWizardDetector, is solely present to make the Account login work on both 1.6 and 2.0
 */
public abstract class WizardDetector {
    /**
     * <p>
     * getClass
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a {@link java.lang.Class} object.
     */
    public static Class getClass(Context context) {
        final int sdkVersion = Build.VERSION.SDK_INT;
        Class wizardClass = WizardActivity.class;
        if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
            wizardClass = WizardActivity.class;
        } else {
            try {
                wizardClass = Class.forName("com.imaginea.android.sugarcrm.WizardAuthActivity");
            } catch (ClassNotFoundException ce) {
                ce.printStackTrace();
            }
        }

        return wizardClass;
    }
}
