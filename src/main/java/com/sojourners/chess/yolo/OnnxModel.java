package com.sojourners.chess.yolo;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import com.sojourners.chess.config.Properties;
import com.sojourners.chess.util.PathUtils;

import java.awt.image.BufferedImage;

/**
 * ONNX模型抽象基类，用于实现象棋识别功能
 * 提供模型加载、棋盘位置检测和棋子识别的基础功能
 */
public abstract class OnnxModel {

    /**
     * 棋盘检测时的padding比例
     */
    public static final double PADDING = 0.8d;

    /**
     * 目标检测的置信度阈值
     */
    public final float CONFIDENCE = 0.75f;

    /**
     * 模型输入图像的尺寸
     */
    public final int SIZE = 640;

    /**
     * 棋子标签数组，包含所有可能的棋子类型
     * 0表示背景，其他字符表示不同的棋子
     */
    public static final char[] labels = {'n', 'b', 'a', 'k', 'r', 'c', 'p', 'R', 'N', 'A', 'K', 'B', 'C', 'P', '0'};

    OrtSession session;

    OrtEnvironment env;

    /**
     * 构造函数，初始化ONNX模型环境并加载模型
     */
    public OnnxModel() {
        try {
            env = OrtEnvironment.getEnvironment();

            OrtSession.SessionOptions opt = new OrtSession.SessionOptions();
            opt.setIntraOpNumThreads(Properties.getInstance().getLinkThreadNum());

            String path = PathUtils.getJarPath() + getModelPath();

            session = env.createSession(path, opt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模型文件路径
     * @return 模型文件路径
     */
    public abstract String getModelPath();

    /**
     * 在图像中查找棋盘位置
     * @param img 输入图像
     * @return 棋盘位置矩形，如果未找到返回null
     */
    public abstract java.awt.Rectangle findBoardPosition(BufferedImage img);

    /**
     * 识别棋盘上的棋子及其位置
     * @param img 输入图像
     * @param board 用于存储识别结果的棋盘数组
     * @return 是否成功识别
     */
    public abstract boolean findChessBoard(BufferedImage img, char[][] board);

}
