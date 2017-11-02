package doext.implement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import core.DoServiceContainer;
import core.helper.DoIOHelper;
import core.helper.DoJsonHelper;
import core.helper.DoScriptEngineHelper;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.interfaces.DoIBitmap;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoMultitonModule;
import core.object.DoSingletonModule;
import doext.define.do_Device_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现Do_Device_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.model.getUniqueKey());
 */
public class do_Device_Model extends DoSingletonModule implements do_Device_IMethod {

	public do_Device_Model() throws Exception {
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
		if ("vibrate".equals(_methodName)) {
			vibrate(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("beep".equals(_methodName)) {
			beep(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("flash".equals(_methodName)) {
			flash(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("getInfo".equals(_methodName)) {
			getInfo(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}

		if ("getAllAppInfo".equals(_methodName)) {
			getAllAppInfo(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}

		if ("getLocale".equals(_methodName)) {
			getLocale(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("home".equals(_methodName)) {
			home(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("setScreenAutoDarken".equals(_methodName)) {
			setScreenAutoDarken(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("getBattery".equals(_methodName)) {
			getBattery(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	@Override
	public void getBattery(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) {
		Intent batteryIntent = DoServiceContainer.getPageViewFactory().getAppContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float result = 50.0f;
		// Error checking that probably isn't needed but I added just in case.
		if (level != -1 && scale != -1) {
			result = ((float) level / (float) scale) * 100.0f;
		}
		_invokeResult.setResultInteger((int) result);
	}

	@Override
	public void getLocale(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		// {'country':'系统国家','language':'当前语言'}
		Locale _locale = DoServiceContainer.getPageViewFactory().getAppContext().getResources().getConfiguration().locale;
		String _country = _locale.getCountry();
		String _language = _locale.getLanguage();

		JSONObject _value = new JSONObject();
		_value.put("country", _country);
		_value.put("language", _language);

		_invokeResult.setResultNode(_value);
	}

	@Override
	public void home(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		DoServiceContainer.getPageViewFactory().getAppContext().startActivity(intent);

	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @throws Exception
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.model.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		if ("screenShot".equals(_methodName)) {
			this.screenShot(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("srceenShotAsBitmap".equals(_methodName)) {
			this.srceenShotAsBitmap(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("getGPSInfo".equals(_methodName)) {
			this.getGPSInfo(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		if ("getRingerMode".equals(_methodName)) {
			this.getRingerMode(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 短音提示；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void beep(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Ringtone alertRingtone = getRingtone(DoServiceContainer.getPageViewFactory().getAppContext());
		if (alertRingtone != null && !alertRingtone.isPlaying()) {
			alertRingtone.play();
		}
	}

	/**
	 * 开关闪光灯；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void flash(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _status = DoJsonHelper.getString(_dictParas, "status", "");
		if (null != _status && "on".equals(_status)) {
			if (camera == null) {
				camera = Camera.open();
			}
			if (!isopen) {
				// 开始亮灯。通过setFlashMode()及startPreview()两种方式确保亮灯。
				Parameters params = camera.getParameters();
				params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(params);
				camera.setPreviewTexture(new SurfaceTexture(0));
				camera.startPreview();
				isopen = true;
			}
		} else if (null != _status && "off".equals(_status)) {
			if (camera != null) {
				// 灭灯。通过setFlashMode()及stopPreview()两种方式确保灭灯。
				Parameters params = camera.getParameters();
				params.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(params);
				camera.stopPreview();
				camera.release(); // 关掉照相机
				camera = null;
				isopen = false;
			}
		}
	}

	/**
	 * 截屏；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void screenShot(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		String _rect = DoJsonHelper.getString(_dictParas, "rect", "");
		DoInvokeResult _invokeResult = new DoInvokeResult(getUniqueKey());
		_invokeResult.setResultText(takeScreenShot(_scriptEngine, _rect));
		_scriptEngine.callback(_callbackFuncName, _invokeResult);
	}

	public void srceenShotAsBitmap(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		String _address = DoJsonHelper.getString(_dictParas, "bitmap", "");
		String _rect = DoJsonHelper.getString(_dictParas, "rect", "");
		if (_address == null || _address.length() <= 0)
			throw new Exception("bitmap参数不能为空！");
		DoMultitonModule _multitonModule = DoScriptEngineHelper.parseMultitonModule(_scriptEngine, _address);
		if (_multitonModule == null)
			throw new Exception("bitmap参数无效！");
		if (_multitonModule instanceof DoIBitmap) {
			DoIBitmap _bitmap = (DoIBitmap) _multitonModule;
			takeScreenShot(_scriptEngine, _rect, _bitmap);
			DoInvokeResult _invokeResult = new DoInvokeResult(getUniqueKey());
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	// 获取状态栏高度
	private int getStatuHeight(Activity activity) {
		Rect _rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(_rect);
		return _rect.top;
	}

	// 获取指定Activity的截屏，保存到png文件
	private void takeScreenShot(DoIScriptEngine _scriptEngine, String _rectStr, DoIBitmap _bitmap) throws IOException {
		Activity _activity = DoServiceContainer.getPageViewFactory().getAppContext();
		Rect _rect = getRect(_scriptEngine, _rectStr);
		int _top = 0;
		if (_rect == null) {
			_rect = new Rect();
			_activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(_rect);
		} else {
			// 如果不是默认，需要减去状态栏
			_top = getStatuHeight(_activity);
		}
		_top += _rect.top;
		// View是你需要截图的View
		View _view = _activity.getWindow().getDecorView();
		_view.setDrawingCacheEnabled(true);
		_view.buildDrawingCache();

		Bitmap _mData = Bitmap.createBitmap(_view.getDrawingCache(), _rect.left, _top, _rect.width(), _rect.height());
		_view.destroyDrawingCache();
		_bitmap.setData(_mData);

	}

	// 获取指定Activity的截屏，保存到png文件
	private String takeScreenShot(DoIScriptEngine _scriptEngine, String _rectStr) throws IOException {
		Activity _activity = DoServiceContainer.getPageViewFactory().getAppContext();
		Rect _rect = getRect(_scriptEngine, _rectStr);
		int _top = 0;
		if (_rect == null) {
			_rect = new Rect();
			_activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(_rect);
		} else {
			// 如果不是默认，需要减去状态栏
			_top = getStatuHeight(_activity);
		}
		_top += _rect.top;
		// View是你需要截图的View
		View _view = _activity.getWindow().getDecorView();
		_view.setDrawingCacheEnabled(true);
		_view.buildDrawingCache();

		Bitmap _mBitmap = Bitmap.createBitmap(_view.getDrawingCache(), _rect.left, _top, _rect.width(), _rect.height());
		_view.destroyDrawingCache();
		ByteArrayOutputStream _mData = new ByteArrayOutputStream();
		SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
		String _fileName = _sdf.format(new Date()) + ".png.do";
		String _fullFileName = _scriptEngine.getCurrentApp().getDataFS().getRootPath() + "/temp/do_Device/" + _fileName;
		try {
			if (null != _mBitmap) {
				_mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, _mData);
			}
			DoIOHelper.writeAllBytes(_fullFileName, _mData.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (_mData != null) {
				_mData.close();
				_mData = null;
			}

			if (_mBitmap != null && !_mBitmap.isRecycled()) {
				_mBitmap.recycle();
				_mBitmap = null;
			}

			System.gc();
		}

		File _file = new File(_fullFileName);
		if (_file.exists() && _file.length() > 0) {
			return "data://temp/do_Device/" + _fileName;
		} else {
			return null;
		}
	}

	private Rect getRect(DoIScriptEngine _scriptEngine, String _rect) {
		Rect _myRect = null;
		if (!TextUtils.isEmpty(_rect)) {
			String[] _data = _rect.split(",");
			if (_data != null && _data.length == 4) {
				// x,y,width,height
				final double _xZoom = _scriptEngine.getCurrentPage().getRootView().getXZoom();
				final double _yZoom = _scriptEngine.getCurrentPage().getRootView().getYZoom();
				int _left = (int) DoUIModuleHelper.getCalcValue(DoTextHelper.strToDouble(_data[0], 0) * _xZoom);
				int _top = (int) DoUIModuleHelper.getCalcValue(DoTextHelper.strToDouble(_data[1], 0) * _yZoom);
				int _right = (int) DoUIModuleHelper.getCalcValue(DoTextHelper.strToDouble(_data[2], 0) * _xZoom) + _left;
				int _bottom = (int) DoUIModuleHelper.getCalcValue(DoTextHelper.strToDouble(_data[3], 0) * _yZoom) + _top;
				_myRect = new Rect(_left, _top, _right, _bottom);
			}
		}
		return _myRect;
	}

	/**
	 * 设备振动；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void vibrate(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		int _duration = DoTextHelper.strToInt(DoJsonHelper.getString(_dictParas, "duration", ""), 1000);
		Vibrator _vibrator = (Vibrator) DoServiceContainer.getPageViewFactory().getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
		_vibrator.vibrate(_duration);
	}

	/**
	 * 获取设备信息；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void getInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Context _ctx = DoServiceContainer.getPageViewFactory().getAppContext();
		TelephonyManager _tm = (TelephonyManager) _ctx.getSystemService(Context.TELEPHONY_SERVICE);
		DisplayMetrics _dm = _ctx.getResources().getDisplayMetrics();
		WindowManager _wm = (WindowManager) _ctx.getSystemService(Context.WINDOW_SERVICE);

		WifiManager wifi = (WifiManager) _ctx.getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifi.getConnectionInfo();

		String _deviceId = _tm.getDeviceId();

		JSONObject _node = new JSONObject();
		if (TextUtils.isEmpty(_deviceId)) {
			_node.put("deviceId", info.getMacAddress());
		} else {
			_node.put("deviceId", _deviceId);
		}
		_node.put("deviceName", android.os.Build.MODEL);
		_node.put("OS", "android");
		_node.put("OSVersion", android.os.Build.VERSION.RELEASE);
		_node.put("resolutionH", _dm.widthPixels + "");
		_node.put("resolutionV", _dm.heightPixels + "");
		_node.put("dpiH", _dm.xdpi + "");
		_node.put("dpiV", _dm.ydpi + "");
		_node.put("screenH", _wm.getDefaultDisplay().getWidth() + "");
		_node.put("screenV", _wm.getDefaultDisplay().getHeight() + "");
		_node.put("phoneType", _tm.getPhoneType() + "");
		_node.put("phoneNumber", _tm.getLine1Number());
		_node.put("communicationType", _tm.getNetworkOperatorName());
		_node.put("simSerialNumber", _tm.getSimSerialNumber());
		_node.put("IMSI", _tm.getSubscriberId());
		_node.put("sdkVersion", android.os.Build.VERSION.SDK);
		_invokeResult.setResultNode(_node);
	}

	/**
	 * 获取手机上所有安装应用的信息；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void getAllAppInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Context _ctx = DoServiceContainer.getPageViewFactory().getAppContext();
		List<PackageInfo> _pinfos = _ctx.getPackageManager().getInstalledPackages(0);
		JSONArray _resultArray = new JSONArray();
		for (PackageInfo _info : _pinfos) {
			String _appName = _info.applicationInfo.loadLabel(_ctx.getPackageManager()).toString();
			String _pName = _info.applicationInfo.packageName;
			int _isSystem = (_info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ? 0 : 1;
			// 获取每个应用程序在操作系统内的进程id
			int _uId = _info.applicationInfo.uid;
			// 如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
			long _traffic = TrafficStats.getUidRxBytes(_uId) + TrafficStats.getUidTxBytes(_uId);
			if (_traffic < 0) {
				_traffic = 0;
			}
			JSONObject _node = new JSONObject();
			_node.put("name", _appName);
			_node.put("pname", _pName);
			_node.put("isSystem", _isSystem);
			_node.put("traffic", _traffic + "");
			_resultArray.put(_node);
		}
		_invokeResult.setResultArray(_resultArray);
	}

	private Ringtone alertRingtone;

	private Ringtone getRingtone(Activity activity) {
		if (alertRingtone == null) {
			alertRingtone = RingtoneManager.getRingtone(activity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		}
		return alertRingtone;
	}

	public void releaseRingtong() {
		if (alertRingtone != null) {
			alertRingtone.stop();
			alertRingtone = null;
		}
	}

	private Camera camera = null;
	private boolean isopen = false;

	@Override
	public void setScreenAutoDarken(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		boolean _isAuto = DoJsonHelper.getBoolean(_dictParas, "isAuto", true);
		if (!_isAuto) {
			DoServiceContainer.getPageViewFactory().getAppContext().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			DoServiceContainer.getPageViewFactory().getAppContext().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	public void getGPSInfo(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {

		Context _ctx = DoServiceContainer.getPageViewFactory().getAppContext();
		int _isOpen = 0;
		LocationManager _manager = (LocationManager) _ctx.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean _gpsProvider = _manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		// boolean _networkProvider = _manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (_gpsProvider) {
			_isOpen = 1;
		}

		DoInvokeResult _invokeResult = new DoInvokeResult(do_Device_Model.this.getUniqueKey());
		try {
			JSONObject _result = new JSONObject();
			_result.put("state", _isOpen);
			_invokeResult.setResultNode(_result);
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("do_Device_Model \n\t", e);
		}
	}

	@Override
	public void getRingerMode(JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
		AudioManager _audioManager = (AudioManager) _context.getSystemService(_context.AUDIO_SERVICE);
		int _ringerMode = _audioManager.getRingerMode();
		JSONObject _result = new JSONObject();
		DoInvokeResult _invokeResult = new DoInvokeResult(do_Device_Model.this.getUniqueKey());
		try {
			if (_ringerMode == AudioManager.RINGER_MODE_NORMAL) {
				_result.put("mode", 1);
			} else {
				_result.put("mode", 0);
			}
			_invokeResult.setResultNode(_result);
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("do_Device_Model \n\t", e);
		}

	}
}