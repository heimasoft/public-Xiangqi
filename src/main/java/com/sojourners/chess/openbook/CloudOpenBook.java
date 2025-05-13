package com.sojourners.chess.openbook;

import com.sojourners.chess.config.Properties;
import com.sojourners.chess.model.BookData;
import com.sojourners.chess.util.HttpUtils;
import com.sojourners.chess.util.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 云库实现类，通过访问chessdb.cn的API获取棋谱数据
 */
public class CloudOpenBook implements OpenBook {

    /**
     * 云库API的基础URL
     */
    private final static String URL = "http://www.chessdb.cn/chessdb.php";

    @Override
    public List<BookData> get(char[][] board, boolean redGo)  {
        // 该方法未实现，因为云库API只支持FEN格式查询
        return null;
    }

    @Override
    public List<BookData> get(String fenCode, boolean onlyFinalPhase) {
        // 从云库获取指定FEN局面的所有可能走法
        List<BookData> list = new ArrayList<>();
        try {
            // 构建查询参数，使用URL编码确保FEN字符串安全传输
            String content = "action=queryall&board=" + URLEncoder.encode(fenCode, "UTF-8");
            // 发送GET请求获取云库数据，使用配置的超时时间
            String result = HttpUtils.sendByGet(URL, content, Properties.getInstance().getCloudBookTimeout());
            System.out.println(result); // 调试输出原始响应

            // 检查响应是否有效且包含走法数据
            if (StringUtils.isNotEmpty(result) && result.contains("move")) {
                // 分割每条走法数据
                String[] datas = result.split("\\|");
                // 解析每条走法数据
                for (String data : datas) {

                    BookData bd = new BookData();
                    bd.setSource("云库"); // 设置数据来源
                    // 分割走法属性
                    String[] items = data.split(",");
                    boolean finalPhase = false; // 标记是否为终局阶段
                    // 解析每个属性键值对
                    for (String item : items) {
                        String[] kvs = item.split(":");
                        // 根据键设置对应的BookData属性
                        if ("move".equals(kvs[0])) {
                            bd.setMove(kvs[1]); // 设置走法
                        } else if ("score".equals(kvs[0])) {
                            bd.setScore(Integer.parseInt(kvs[1])); // 设置分数
                        } else if ("winrate".equals(kvs[0])) {
                            bd.setWinRate(Double.parseDouble(kvs[1])); // 设置胜率
                        } else if ("note".equals(kvs[0])) {
                            bd.setNote(kvs[1]); // 设置备注
                            // 检查备注是否包含终局标记(W/D/L)
                            if (kvs[1].contains("W") || kvs[1].contains("D") || kvs[1].contains("L")) {
                                finalPhase = true;
                            }
                        }
                    }
                    // 根据配置决定是否只添加终局阶段的数据
                    if (!(onlyFinalPhase || Properties.getInstance().getOnlyCloudFinalPhase()) || finalPhase) {
                        list.add(bd);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void close() {
        // 云库连接无需关闭，空实现
    }

}