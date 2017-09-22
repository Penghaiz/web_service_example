/**
 * 说明：请开发者完善...
 * 作者：章鹏海
 * 时间：2013-10-29
 */
package lycmcc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.axis2.AxisFault;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import lycmcc.service.LoidInfoServiceServiceStub;
import lycmcc.service.LoidInfoServiceServiceStub.AddZhangHao;
import lycmcc.service.LoidInfoServiceServiceStub.AddZhangHaoE;

public class LoidDataUpdate {

	/**
	 * @param args
	 *            下午4:33:22
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void main(String[] args) {

		Timer timer = new Timer();
		timer.schedule(task, 10000, 600000);

		// http://10.52.136.18:7899/lycmcc/kuandaiuser.lycmcc?act=show_kuandaiuser&begin_time=2013-11-03&dingdan_type=&end_time=2013-11-04&flow_home_county=%E6%96%B0%E7%BD%97&flow_id=&flow_status=&limit=20&loid=&operator_flag=&other_handle=&start=0&user_zhanghao=

	}

	static TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			sendPost();
		}

	};

	public static void addZhangHao(String zhanghao) {
		try {
			LoidInfoServiceServiceStub stub = new LoidInfoServiceServiceStub();
			AddZhangHaoE addZhangHaoE = new AddZhangHaoE();
			AddZhangHao addZhangHao = new AddZhangHao();
			addZhangHao.setZhanghao(zhanghao);
			addZhangHaoE.setAddZhangHao(addZhangHao);
			try {
				stub.addZhangHao(addZhangHaoE);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendPost() {
		String result = "";
		String url = "http://10.53.160.148:7899/lycmcc/kuandaiuser.lycmcc?act=show_kuandaiuser&tongbu_flag=0&start=0&limit=99999";
		// url="http://10.53.160.148:7899/lycmcc/kuandaiuser.lycmcc?act=show_kuandaiuser&tongbu_flag=0&start=0&limit=99999";
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl
					.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			PrintWriter out = new PrintWriter(httpConn.getOutputStream());
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream(), "UTF-8"));
			String line;
			JSONObject objnew = null;
			JSONObject obj = null;
			JSONArray array = null;
			StringBuffer ids = new StringBuffer();
			while ((line = in.readLine()) != null) {
				result += line;
				obj = JSONObject.fromObject(result);
				array = obj.getJSONArray("records");
				if(array.size() == 0){
					System.out.println("没有需要同步的数据！");
					return;
				}
				for (int i = 0; i < array.size(); i++) {
					objnew = JSONObject.fromObject(array.getString(i));
					System.out.println(objnew.get("user_zhanghao"));
					addZhangHao(objnew.get("user_zhanghao").toString());
					ids.append("'"+objnew.get("id") + "',");
				}
			}
			in.close();
			// 更新同步标志
			URL updateHttpUrl = new URL(
					"http://10.53.160.148:7899/lycmcc/kuandaiuser.lycmcc?act=update_tongbu_flag&ids="
							+ ids.substring(0, ids.length() - 1));
			HttpURLConnection updateHttpConn = (HttpURLConnection) updateHttpUrl
					.openConnection();
			updateHttpConn.setDoOutput(true);
			PrintWriter updateOut = new PrintWriter(
					updateHttpConn.getOutputStream());
			updateOut.flush();
			updateOut.close();
			InputStream updateIn = updateHttpConn.getInputStream();
			updateIn.close();
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			System.out.println(df.format(new Date())+"更新完毕。");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("系统异常！");
		}
		;
	}
}
