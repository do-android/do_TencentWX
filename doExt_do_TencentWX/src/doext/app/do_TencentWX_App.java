package doext.app;

import android.content.Context;
import core.interfaces.DoIAppDelegate;

/**
 * APP启动的时候会执行onCreate方法；
 * 
 */
public class do_TencentWX_App implements DoIAppDelegate {

	private static do_TencentWX_App instance;

	private do_TencentWX_App() {

	}

	public static do_TencentWX_App getInstance() {
		if (instance == null) {
			instance = new do_TencentWX_App();
		}
		return instance;
	}

	@Override
	public void onCreate(Context context) {
		// ...do something
	}

	public String getModuleTypeID() {
		return "do_TencentWX";
	}

	@Override
	public String getTypeID() {
		return getModuleTypeID();
	}
}
