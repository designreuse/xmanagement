package com.tq.management.base.system.service.impl;

import java.io.File;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tq.management.base.entity.FileInfo;
import com.tq.management.base.enums.FileEnums;
import com.tq.management.base.system.mapper.FileInfoMapper;
import com.tq.management.base.system.service.FileInfoService;
import com.tq.management.base.utils.BigFileMD5;
import com.tq.management.base.utils.CrudUtils;
import com.tq.management.base.utils.DateUtils;
import com.tq.management.base.utils.RandomUtils;
import com.tq.management.spring.utils.SystemParamUtil;

/**
 * @version 1.0
 * @author tangqian
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

	private static Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

	@Resource
	private FileInfoMapper mapper;

	private static final String UPLOAD_ABSOLUTE_PATE = SystemParamUtil.getWebRoot() + "upload/";

	@Override
	public FileInfo add(MultipartFile file, FileEnums.TypeEnum typeEnum, FileEnums.TempEnum tempEnum) {
		if (file == null || file.getSize() == 0)
			return null;

		String fileName = file.getOriginalFilename();
		String ext = getExt(fileName);

		String relativePath = generatePath(typeEnum);
		File targetFile = generateNewFile(relativePath, ext);
		
		try {
			file.transferTo(targetFile);
		} catch (Exception e) {
			logger.error("保存文件失败", e);
		}

		FileInfo fileInfo = new FileInfo(relativePath + targetFile.getName(), fileName, ext,
				targetFile.length(), file.getContentType(), BigFileMD5.getMD5(targetFile),
				typeEnum.getCode(), tempEnum.getCode());
		CrudUtils.beforeAdd(fileInfo);
		mapper.insert(fileInfo);
		return fileInfo;
	}

	public String getAbsolutePath(String relativePath) {
		return UPLOAD_ABSOLUTE_PATE + relativePath;
	}

	/**
	 * 文件后缀统一小写
	 * 
	 * @param fileName
	 * @return
	 */
	private String getExt(String fileName) {
		return fileName.lastIndexOf('.') != -1 ? fileName.substring(fileName.lastIndexOf('.') + 1)
				.toLowerCase() : "";
	}

	private File generateNewFile(String relativePath, String ext) {
		String newName = generateRandomName(ext);
		File targetFile = new File(getAbsolutePath(relativePath), newName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		} else {
			while (targetFile.exists()) {
				newName = generateRandomName(ext);
				targetFile = new File(getAbsolutePath(relativePath), newName);
			}
		}
		return targetFile;
	}

	private String generatePath(FileEnums.TypeEnum typeEnum) {
		Date now = new Date();
		StringBuilder sb = new StringBuilder(typeEnum.getClassifyDir()).append('/')
				.append(DateUtils.formatDate(now, "yyyy-MM")).append('/');
		return sb.toString();
	}

	private String generateRandomName(String ext) {
		return StringUtils.isEmpty(ext) ? RandomUtils.getRandomString(8) : RandomUtils
				.getRandomString(8) + "." + ext;
	}

	@Override
	public FileInfo get(Integer id) {

		return null;
	}

}
