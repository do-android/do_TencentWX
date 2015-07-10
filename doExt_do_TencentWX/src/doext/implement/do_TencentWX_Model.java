package doext.implement;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import core.DoServiceContainer;
import core.helper.DoIOHelper;
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
	
	public static final String LOGIN_FLAG = "login";
	public static final String PAY_FLAG = "pay";
	public static final String SHARE_FLAG = "share";
	
	public static String OPERAT_FLAG  = LOGIN_FLAG;
	
	
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
			OPERAT_FLAG  =  LOGIN_FLAG;
			this.login(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("pay".equals(_methodName)) {
			OPERAT_FLAG  =  PAY_FLAG;
			this.pay(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("share".equals(_methodName)) {
			OPERAT_FLAG  =  SHARE_FLAG;
			this.share(_dictParas, _scriptEngine, _callbackFuncName);
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

	public void callBack(BaseResp baseResp) throws Exception {
		
		DoInvokeResult _invokeResult = new DoInvokeResult(getUniqueKey());
		if (baseResp instanceof SendAuth.Resp) {
			SendAuth.Resp resp = (SendAuth.Resp) baseResp;
			JSONObject _node = new JSONObject();
			_node.put("errCode", resp.errCode);
			_node.put("code", resp.code);
			_node.put("state", resp.state);
			_node.put("lang", resp.lang);
			_node.put("country", resp.country);
			_invokeResult.setResultNode(_node);
			
		}else if(baseResp instanceof SendMessageToWX.Resp){
			SendMessageToWX.Resp resp = (SendMessageToWX.Resp) baseResp;
			//分享成功
			if(resp.errCode == BaseResp.ErrCode.ERR_OK){
				_invokeResult.setResultBoolean(true);
			//分享失败
			}else{
				_invokeResult.setResultBoolean(false);
			}
		}else if(baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
			if(baseResp.errCode == 0){
				_invokeResult.setResultInteger(Integer.parseInt("0"));
			}else if(baseResp.errCode == -2){
				_invokeResult.setResultInteger(Integer.parseInt("-2"));
			}else {
				_invokeResult.setResultInteger(Integer.parseInt("-1"));
			}
		}
		scriptEngine.callback(callbackFuncName, _invokeResult);
	}

	/**
	 * 使用微信支付；
	 * 
	 * @throws Exception
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void pay(JSONObject _dictParas, DoIScriptEngine _scriptEngine,
			String _callbackFuncName) throws Exception {
		this.scriptEngine = _scriptEngine;
		this.callbackFuncName = _callbackFuncName;
		String _appId = DoJsonHelper.getString(_dictParas,"appId", "");
		String _partnerId = DoJsonHelper.getString(_dictParas,"partnerId", "");
		String _prepayId = DoJsonHelper.getString(_dictParas,"prepayId", "");
		String _package = DoJsonHelper.getString(_dictParas,"package", "");
		String _nonceStr = DoJsonHelper.getString(_dictParas,"nonceStr", "");
		String _timeStamp = DoJsonHelper.getString(_dictParas,"timeStamp", "");
		String _sign = DoJsonHelper.getString(_dictParas,"sign", "");
		Activity _activity = DoServiceContainer.getPageViewFactory().getAppContext();
		String _packageName = _activity.getPackageName();
		ComponentName _componetName = new ComponentName(_packageName, _packageName + ".wxapi.WXPayEntryActivity");
		Intent i = new Intent();
		i.putExtra("appId", _appId);
		i.putExtra("partnerId", _partnerId);
		i.putExtra("prepayId", _prepayId);
		i.putExtra("package", _package);
		i.putExtra("nonceStr", _nonceStr);
		i.putExtra("timeStamp", _timeStamp);
		i.putExtra("sign", _sign);
		i.setComponent(_componetName);
		_activity.startActivity(i);
	}
	
	/**
	 * 使用微信分享；
	 * 
	 * @throws Exception
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void share(JSONObject _dictParas, DoIScriptEngine _scriptEngine,
			String _callbackFuncName) throws Exception {

		this.scriptEngine = _scriptEngine;
		this.callbackFuncName = _callbackFuncName;
		String _appId = DoJsonHelper.getString(_dictParas,"appId", "");
		int _scene = DoJsonHelper.getInt(_dictParas, "scene", 0);
		int _type = DoJsonHelper.getInt(_dictParas,"type", 0);
		String _title = DoJsonHelper.getString(_dictParas,"title", "");
		String _content = DoJsonHelper.getString(_dictParas,"content", "");
		String _url = DoJsonHelper.getString(_dictParas,"url", "");
		String _image = DoJsonHelper.getString(_dictParas,"image", "");
		String _audio = DoJsonHelper.getString(_dictParas,"audio", "");
		
		if (!_image.equals("")&&null == DoIOHelper.getHttpUrlPath(_image)) {
			_image = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _image);
		}
		
		Activity _activity = DoServiceContainer.getPageViewFactory().getAppContext();
		String _packageName = _activity.getPackageName();
		ComponentName _componetName = new ComponentName(_packageName, _packageName + ".wxapi.WXEntryActivity");
		Intent i = new Intent();
		i.putExtra("appId", _appId);
		i.putExtra("scene", _scene);
		i.putExtra("type", _type);
		i.putExtra("title", _title);
		i.putExtra("content", _content);
		i.putExtra("url", _url);
		i.putExtra("image", _image);
		i.putExtra("audio", _audio);
		i.setComponent(_componetName);
		_activity.startActivity(i);
	}
	
}