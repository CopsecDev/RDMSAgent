package com.copsec.railway.rms.fileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Optional;

import com.copsec.railway.rms.beans.FilePropertyBean;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.sigontanPools.LogFilePropertyPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件操作
 */
public class FileReaderUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileReaderUtils.class);

	public static Optional<String> readerContent(String filePath){

		try(BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))){

			String line = null;
			StringBuilder sb = new StringBuilder();
			while( (line = reader.readLine()) != null){

				sb.append(line);
			}
			return Optional.ofNullable(sb.toString());
		}
		catch (FileNotFoundException e) {

			logger.error(e.getMessage(),e);
			return Optional.ofNullable(null);
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
		}
		return Optional.ofNullable(null);
	}

	/**
	 * 文件拷贝
	 * @param sourcesFilePath
	 * @param destFilePath
	 * @return
	 */
	public static void copyFile(String sourcesFilePath,String destFilePath) throws Exception{

		try(InputStream inputStream = new FileInputStream(new File(sourcesFilePath));
			OutputStream outputStream = new FileOutputStream(new File(destFilePath))) {

			FileChannel sourceChannel = ((FileInputStream) inputStream).getChannel();

			FileChannel destChannel = ((FileOutputStream) outputStream).getChannel();

			destChannel.transferFrom(sourceChannel,0,sourceChannel.size());

			destChannel.close();

			sourceChannel.close();
		}catch (Throwable e){

			throw e;
		}
	}

	public static String copyCertFiles(String deviceId,String tmpPath,String instanceName){

		String db8path = StatisResources.webProxy40_cert_path + instanceName + "-" + deviceId + "-cert8.db";

		String key3path = StatisResources.webProxy40_cert_path + instanceName + "-" + deviceId + "-key3.db";

		String tmpInstanceName = tmpPath + File.separator + instanceName;
		File dir = new File(tmpInstanceName);
		if(!dir.exists()){

			dir.mkdirs();
		}
		String destDb8Path = tmpInstanceName + File.separator + "cert8.db";
		String destKey3Path = tmpInstanceName + File.separator + "key3.db";

		try {

			copyFile(db8path,destDb8Path);

			copyFile(key3path,destKey3Path);

		}catch (Throwable t){

			logger.error(t.getMessage(),t);
			return null;
		}

		return tmpInstanceName;
	}

	public static void copyFileWithFileProtities(File sources,File outFile,FilePropertyBean filePropertyBean){

		try(FileChannel inputChannel = new FileInputStream(sources).getChannel();
			FileChannel outputChannel = new FileOutputStream(outFile).getChannel()){

			if(!outFile.exists()){

				outFile.createNewFile();
			}
			if(sources.lastModified() > filePropertyBean.getLastModified()) //文件有更新内容
			{

				if(inputChannel.size() < filePropertyBean.getPosition() ){

					filePropertyBean.setPosition(0L);
				}
				inputChannel.transferTo(filePropertyBean.getPosition(), inputChannel.size(), outputChannel);
				filePropertyBean.setPosition(inputChannel.size());
				filePropertyBean.setLastModified(sources.lastModified());
				LogFilePropertyPools.getInstance().update(filePropertyBean);

				if(logger.isDebugEnabled()){

					logger.debug("copy file from -> {} to -> {}",sources.getName(),outFile.getName());
				}
			}
		}
		catch (FileNotFoundException e) {

			logger.error(e.getMessage(),e);
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);

		}

	}
}
