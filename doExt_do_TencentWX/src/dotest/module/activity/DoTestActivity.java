package dotest.module.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.doext.module.activity.R;

import core.DoServiceContainer;
import core.object.DoModule;
import doext.implement.do_TencentWX_Model;
import dotest.module.frame.debug.DoPageViewFactory;
import dotest.module.frame.debug.DoService;

/**
 * 测试扩展组件Activity需继承此类，并重写相应测试方法；
 */
public class DoTestActivity extends Activity {
	
	protected DoModule model;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceone_test);
		DoService.Init();
		DoPageViewFactory doPageViewFactory = (DoPageViewFactory)DoServiceContainer.getPageViewFactory();
		doPageViewFactory.setCurrentActivity(this);
		try {
			initModuleModel();
			initUIView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		onEvent();
	}

	/**
	 * 初始化UIView，扩展组件是UIModule类型需要重写此方法；
	 */
	protected void initUIView() throws Exception{

	}

	/**
	 * 初始化Model对象
	 */
	protected void initModuleModel() throws Exception {
		model = new do_TencentWX_Model();
	}

	/**
	 * 测试属性
	 * 
	 * @param view
	 */
	public void doTestProperties(View view) {

	}

	/**
	 * 测试（同步/异步）方法
	 * 
	 * @param view
	 */
	public void doTestMethod(View view) {
		doTestSyncMethod();
		doTestAsyncMethod();
	}

	/**
	 * 测试同步方法
	 */
	protected void doTestSyncMethod() {

	}

	/**
	 * 测试异步方法
	 */
	protected void doTestAsyncMethod() {
		Map<String, String>  _paras_loadString = new HashMap<String, String>();
        _paras_loadString.put("appId", "wx7589880f174273b5");
        DoService.ansyncMethod(this.model, "login", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
//				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
		
		
	}

	/**
	 * 测试Module订阅事件消息
	 */
	protected void onEvent() {

	}

	/**
	 * 测试模拟触发一个Module消息事件
	 * 
	 * @param view
	 */
	public void doTestFireEvent(View view) {

	}
}
