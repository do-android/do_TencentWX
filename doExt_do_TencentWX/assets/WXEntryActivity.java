package com.doext.module.activity.wxapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import core.DoServiceContainer;
import doext.app.do_TencentWX_App;
import doext.implement.do_TencentWX_Model;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;
	// 图文分享标识
	public static final int SHARE_IMG_TEXT = 0;
	// 纯图分享标识
	public static final int SHARE_IMG = 1;
	// 音乐分享标识
	public static final int SHARE_MUSIC = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String appId = getIntent().getStringExtra("appId");

		if (appId == null && do_TencentWX_Model.OPERAT_FLAG.equals(do_TencentWX_Model.LOGIN_FLAG)) {
			finish();
			Toast.makeText(this, "appId不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		api = WXAPIFactory.createWXAPI(this, appId);
		api.registerApp(appId);
		api.handleIntent(getIntent(), this);

		if (do_TencentWX_Model.OPERAT_FLAG.equals(do_TencentWX_Model.LOGIN_FLAG)) {
			login();
		} else if (do_TencentWX_Model.OPERAT_FLAG.equals(do_TencentWX_Model.PAY_FLAG)) {
			pay(appId);
		} else if (appId != null && do_TencentWX_Model.OPERAT_FLAG.equals(do_TencentWX_Model.SHARE_FLAG)) {
			share();
		}

	}

	private void login() {
		boolean isFlag = getIntent().getBooleanExtra("isFlag", true);
		if (!isFlag) {
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "deviceone";
			api.sendReq(req);
			this.finish();
		}
	}

	private void pay(String appId) {
		PayReq req = new PayReq();

		String _partnerId = getIntent().getStringExtra("partnerId");
		String _package = getIntent().getStringExtra("package");
		String _nonceStr = getIntent().getStringExtra("nonceStr");
		String _timeStamp = getIntent().getStringExtra("timeStamp");
		String _sign = getIntent().getStringExtra("sign");
		if (checkIsNull("partnerId", _partnerId) || checkIsNull("package", _package) || checkIsNull("nonceStr", _nonceStr) || checkIsNull("timeStamp", _timeStamp) || checkIsNull("sign", _sign)) {
			return;
		} else {
			req.appId = appId;
			req.partnerId = _partnerId;
			req.packageValue = _package;
			req.nonceStr = _nonceStr;
			req.timeStamp = _timeStamp;
			req.sign = _sign;
		}
		api.sendReq(req);
		finish();
	}

	private void share() {
		// 分享场景 0 分享到微信好友，1 分享都微信朋友圈
		int _scene = getIntent().getIntExtra("scene", 0);
		// 分享类型 0 图文分享，1 纯图分享 2 音乐分享
		int _type = getIntent().getIntExtra("type", 0);
		// 分享标题
		String _title = getIntent().getStringExtra("title");
		// 分享文本内容
		String _content = getIntent().getStringExtra("content");
		// 分享后点击文本后打开的地址
		String _url = getIntent().getStringExtra("url");
		// 分享图片路径
		String _image = getIntent().getStringExtra("image");
		// 分享音乐地址
		String _audio = getIntent().getStringExtra("audio");
		switch (_type) {
		case SHARE_IMG_TEXT:
			if (!checkIsNull("图文分享类型，文本内容", _content)) {
				sendImgText(_url, _title, _content, _image, _scene);
			}
			break;
		case SHARE_IMG:
			if (!checkIsNull("纯图分享类型，图片路径", _content)) {
				sendImg(_image, _scene);
			}
			break;
		case SHARE_MUSIC:

			sendMusic(_audio, _scene, _title, _content);
			break;

		default:

			Toast.makeText(this, "微信分享类型不合法，错误类型号：" + _type, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

	}

	@Override
	public void onReq(BaseReq req) {}

	@Override
	public void onResp(BaseResp resp) {

		try {
			do_TencentWX_Model _model = (do_TencentWX_Model) DoServiceContainer.getSingletonModuleFactory().getSingletonModuleByID(null, do_TencentWX_App.getInstance().getModuleTypeID());
			_model.callBack(resp);

		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("do_TencentWX_Model onResp \n\t", e);
		}
		this.finish();

	}

	private boolean checkIsNull(String name, String data) {
		if (data == null) {
			finish();
			Toast.makeText(this, name + "不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return false;
		}

	}

	public void sendImg(String image, int scene) {
		File file = new File(image);
		if (!file.exists()) {
			Toast.makeText(this, image + "不存在", Toast.LENGTH_SHORT).show();
			return;
		}
		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(image);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		Bitmap thumbBmp = getLocalImage(image);

		msg.thumbData = getBitmapBytes(thumbBmp, false);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = scene;
		api.sendReq(req);
		finish();
	}

	public void sendMusic(String audio, int scene, String title, String text) {
		WXMusicObject music = new WXMusicObject();
		music.musicUrl = audio;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = music;
		msg.title = title;
		msg.description = text;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("music");
		req.message = msg;
		req.scene = scene;
		api.sendReq(req);
		finish();
	}

	@SuppressLint("HandlerLeak")
	public void sendImgText(final String url, final String title, final String text, final String image, final int scene) {

		final String imgUrl = image;
		final Handler handler = new Handler() {

			public void handleMessage(android.os.Message msg) {
				Bitmap bmp = (Bitmap) msg.obj;
				toTextImg(url, title, text, bmp, scene);
			};
		};
		// 如果是网络图片
		if (imgUrl.contains("http://") || imgUrl.contains("https://")) {
			// 开启子线程加载图片
			new Thread() {
				public void run() {
					try {
						Bitmap thumb = BitmapFactory.decodeStream(new URL(image).openStream());
						handler.handleMessage(handler.obtainMessage(1, thumb));
					} catch (Exception e) {
						DoServiceContainer.getLogEngine().writeError("do_TencentWX_Model sendImgText \n\t", e);
					}
				};
			}.start();
			// 如果是本地图片
		} else {
			Bitmap bmp = getLocalImage(imgUrl);
			toTextImg(url, title, text, bmp, scene);
		}
	}

	private void toTextImg(String url, String title, String text, Bitmap bmp, int scene) {
		WXWebpageObject localWXWebpageObject = new WXWebpageObject();
		localWXWebpageObject.webpageUrl = url;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(localWXWebpageObject);
		localWXMediaMessage.title = title;
		localWXMediaMessage.description = text;
		if (bmp != null) {
			localWXMediaMessage.thumbData = getBitmapBytes(bmp, false);
		}
		SendMessageToWX.Req localReq = new SendMessageToWX.Req();
		localReq.transaction = buildTransaction("ImgText");
		localReq.message = localWXMediaMessage;
		localReq.scene = scene;
		api.sendReq(localReq);
		finish();
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	private Bitmap getLocalImage(String srcPath) {
		if (srcPath == null || !new File(srcPath).exists()) {
			return null;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	// 压缩图片，使图片不大于100kb
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 需要对图片进行处理，否则微信会在log中输出thumbData检查错误
	private byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
		Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);
		int i;
		int j;
		if (bitmap.getHeight() > bitmap.getWidth()) {
			i = bitmap.getWidth();
			j = bitmap.getWidth();
		} else {
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
		while (true) {
			localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 80, 80), null);
			if (paramBoolean)
				bitmap.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {

			}
			i = bitmap.getHeight();
			j = bitmap.getHeight();
		}
	}
}
