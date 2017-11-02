package doext.define;

import org.json.JSONObject;

import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;

/**
 * 声明自定义扩展组件方法
 */
public interface do_Device_IMethod {
	void beep(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void flash(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void getInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void vibrate(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void getAllAppInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void screenShot(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception;

	void getLocale(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void getGPSInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception;

	void home(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void setScreenAutoDarken(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void getBattery(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult);

	void getRingerMode(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception;
}