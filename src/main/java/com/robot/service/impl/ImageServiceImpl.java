package com.robot.service.impl;

import com.robot.common.CommonConsts;
import com.robot.common.CommonUtils;
import com.robot.dao.ImageDao;
import com.robot.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageDao imageDao;
    @Value("${beauty.img.url}")
    private String beautyImgUrl;

    @Override
    public String getBeautyImgUrl() {
        int randomId = CommonUtils.getInstance().getIntegerRandomId(CommonConsts.getInstance().beautyImgIds);
        String imgName = imageDao.getBeautyImgInfoById(randomId);

        return beautyImgUrl + imgName;
    }
}
