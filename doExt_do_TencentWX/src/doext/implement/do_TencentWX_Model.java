package doext.implement;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;

import core.DoServiceContainer;
import core.helper.DoJsonHelper;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_TencentWX_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_TencentWX_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_TencentWX_Model extends DoSingletonModule implements do_TencentWX_IMethod {

	public do_TencentWX_Model() throws Exception {
		super();
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		// ...do something
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		if ("login".equals(_methodName)) {
			this.login(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 使用微信登录；
	 * 
	 * @throws Exception
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void login(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		this.scriptEngine = _scriptEngine;
		this.callbackFuncName = _callbackFuncName;
		String _appId = DoJsonHelper.getString(_dictParas,"appId", "");
		Activity _activity = DoServiceContainer.getPageViewFactory().getAppContext();
		String _packageName = _activity.getPackageName();
		ComponentName _componetName = new ComponentName(_packageName, _packageName + ".wxapi.WXEntryActivity");
		Intent i = new Intent();
		i.putExtra("appId", _appId);
		i.putExtra("isFlag", false);
		i.setComponent(_componetName);
		_activity.startActivity(i);
	}

	private DoIScriptEngine scriptEngine;
	private String callbackFuncName;

	public void callBack(Resp resp) throws Exception {
		DoInvokeResult _invokeResult = new DoInvokeResult(getUniqueKey());
		JSONObject _node = new JSONObject();
		_node.put("errCode", resp.errCode);
		_node.put("code", resp.code);
		_node.put("state", resp.state);
		_node.put("lang", resp.lang);
		_node.put("country", resp.country);
		_invokeResult.setResultNode(_node);
		scriptEngine.callback(callbackFuncName, _invokeResult);
	}

}