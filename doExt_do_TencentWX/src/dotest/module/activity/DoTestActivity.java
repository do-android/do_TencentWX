package dotest.module.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.doext.module.activity.R;

import core.DoServiceContainer;
import core.object.DoSingletonModule;
import doext.app.do_TencentWX_App;
import doext.implement.do_TencentWX_Model;
import dotest.module.frame.debug.DoPageViewFactory;
import dotest.module.frame.debug.DoService;
import dotest.module.frame.debug.DoSingletonModuleFactory;

/**
 * 测试扩展组件Activity需继承此类，并重写相应测试方法；
 */
public class DoTestActivity extends Activity {
	
	protected DoSingletonModule model;
	private EditText edit_scene;
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
		edit_scene = (EditText) findViewById(R.id.edit_scene);
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
		DoSingletonModuleFactory factory = new DoSingletonModuleFactory();
		DoServiceContainer.setSingletonModuleFactory(factory);
		factory.dictSingletonModules.put(do_TencentWX_App.getInstance().getModuleTypeID(), model);
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
	 * 测试分享图文
	 */
	public void shareImgText(View view) {
		Map<String, String>  _paras_loadString = new HashMap<String, String>();
        _paras_loadString.put("appId", "wx7589880f174273b5");
        _paras_loadString.put("scene", edit_scene.getText().toString());
        _paras_loadString.put("type", "0");
        _paras_loadString.put("title", "测试微信分享");
        _paras_loadString.put("content", "测试微信分享测试微信分享测试微信分享测试微信分享");
        _paras_loadString.put("url", "http://www.baidu.com");
       // _paras_loadString.put("image", "/storage/emulated/0/test.png");
        _paras_loadString.put("image", "http://img.qzone.la/uploads/allimg/110629/co110629135Z1-6.jpg");
        DoService.ansyncMethod(this.model, "share", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
				Toast.makeText(DoTestActivity.this, "异步方法回调：" + _data, Toast.LENGTH_SHORT).show();
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}
	/**
	 * 测试分享纯图
	 */
	public void shareImg(View view) {
		Map<String, String>  _paras_loadString = new HashMap<String, String>();
        _paras_loadString.put("appId", "wx7589880f174273b5");
        _paras_loadString.put("scene", edit_scene.getText().toString());
        _paras_loadString.put("type", "1");
        _paras_loadString.put("url", "http://www.baidu.com");
        _paras_loadString.put("image", "/storage/emulated/0/test.png");
        DoService.ansyncMethod(this.model, "share", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
				Toast.makeText(DoTestActivity.this, "异步方法回调：" + _data, Toast.LENGTH_SHORT).show();
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}
	/**
	 * 测试分享音乐
	 */
	public void shareMusic(View view) {
		Map<String, String>  _paras_loadString = new HashMap<String, String>();
        _paras_loadString.put("appId", "wx7589880f174273b5");
        _paras_loadString.put("scene", edit_scene.getText().toString());
        _paras_loadString.put("type", "2");
        _paras_loadString.put("title", "音乐标题");
        _paras_loadString.put("content", "这是首为你写的歌这是首为你写的歌这是首为你写的歌");
        _paras_loadString.put("audio", "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3");
        DoService.ansyncMethod(this.model, "share", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
				Toast.makeText(DoTestActivity.this, "异步方法回调：" + _data, Toast.LENGTH_SHORT).show();
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}
	
	/**
	 * 测试异步方法
	 */
	protected void doTestAsyncMethod() {
		Map<String, String>  _paras_loadString = new HashMap<String, String>();
		//d0b8c93dc7e3e0a814ed3512520b190c
        _paras_loadString.put("appId", "wx7589880f174273b5");
        
        DoService.ansyncMethod(this.model, "login", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
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
