package trong.lixco.com.thai.apitrong;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import trong.lixco.com.util.StaticPath;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PositionJobDataService {
	public static final String NAME = "vitricongviec";
	static Gson gson;
	static {
		GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Date.class, new DateJsonSerializer());
		gsonBuilder.registerTypeAdapter(Date.class, new DateJsonDeserializer());
		gson = gsonBuilder.create();
	}

	/**
	 * Lay du lieu vi tri cong viec
	 * 
	 * @param method
	 *            tatcavt
	 * @param param
	 *            Khong co tham so truyen vao
	 * @return danh sach vi tri cong viec
	 */
	public static PositionJobData[] tatcaphongban() {
		try {
			String link = "?cm=tatcavt&dt=";
			String data = process(link);
			DataResponseAPI ketqua = gson.fromJson(data, DataResponseAPI.class);
			PositionJobData[] positionJobDatas = gson.fromJson(ketqua.getDt(), PositionJobData[].class);
			return positionJobDatas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Lay vi tri cong viec theo phong ban
	 * 
	 * @param method
	 *            vttheophongban
	 * @param param
	 *            ma phong ban
	 * @return danh sach vi tri cong viec
	 */
	public static PositionJobData[] vttheophongban(String param) {
		try {
			String link = "?cm=vttheophongban&dt=" + param;
			String data = process(link);
			DataResponseAPI ketqua = gson.fromJson(data, DataResponseAPI.class);
			PositionJobData[] positionJobDatas = gson.fromJson(ketqua.getDt(), PositionJobData[].class);
			return positionJobDatas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Lay vi tri cong viec kpi nhom cuoi
	 * 
	 * @param method
	 *            vtkpinhomcuoitheophong
	 * @param param
	 *            ma phong ban
	 * @return danh sach vi tri cong viec
	 */
	public static PositionJobData[] vtkpinhomcuoitheophong(String param) {
		try {
			String link = "?cm=vtkpinhomcuoitheophong&dt=" + param;
			String data = process(link);
			DataResponseAPI ketqua = gson.fromJson(data, DataResponseAPI.class);
			PositionJobData[] positionJobDatas = gson.fromJson(ketqua.getDt(), PositionJobData[].class);
			return positionJobDatas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Lay vi tri cong viec kpi nhom cuoi
	 * 
	 * @param method
	 *            vtkpinhomcuoi
	 * @param param
	 *            khong co param
	 * @return danh sach vi tri cong viec
	 */
	public static PositionJobData[] vtkpinhomcuoi() {
		try {
			String link = "?cm=vtkpinhomcuoi&dt=";
			String data = process(link);
			DataResponseAPI ketqua = gson.fromJson(data, DataResponseAPI.class);
			PositionJobData[] positionJobDatas = gson.fromJson(ketqua.getDt(), PositionJobData[].class);
			return positionJobDatas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Tim vi tri cong viec theo ma
	 * 
	 * @param method
	 *            vttheoma
	 * @param param
	 *            (tham so truyen vao) pbtheoma: ma vi tri
	 * @return vi tri cong viec
	 */
	public static PositionJobData timtheoma(String param) {
		try {
			String link = "?cm=vttheoma&dt=" + param;
			String data = process(link);
			DataResponseAPI ketqua = gson.fromJson(data, DataResponseAPI.class);
			PositionJobData positionJobData = gson.fromJson(ketqua.getDt(), PositionJobData.class);
			return positionJobData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static String process(String link) throws Exception {
		URL url = new URL(StaticPath.getPathCenter() + "/api/" + NAME + link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("token", "c894b18f-6e51-4bf3-9a3b-0a1c2d7d4211");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			return response.toString();
		}
	}

	public static void main(String[] args) {
		try {
			DepartmentData[] result = DepartmentDataService.tatcaphongban("");
			System.out.println(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static PositionJobData[] vttheonhanvien(String param) {
		try {
			String link = "?cm=vttheonhanvien&dt=" + param;
			String data = process(link);
			DataResponseAPI ketqua = gson.fromJson(data, DataResponseAPI.class);
			PositionJobData[] positionJobDatas = gson.fromJson(ketqua.getDt(), PositionJobData[].class);
			return positionJobDatas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
