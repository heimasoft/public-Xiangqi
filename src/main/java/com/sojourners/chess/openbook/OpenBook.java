package com.sojourners.chess.openbook;

import com.sojourners.chess.model.BookData;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 棋谱库接口，定义了从棋谱库中获取棋谱数据的方法
 */
public interface OpenBook {

    /**
 * 根据当前棋盘状态获取棋谱数据
 * @param board 当前棋盘状态
 * @param redGo 是否轮到红方走棋
 * @return 匹配的棋谱数据列表
 */
List<BookData> get(char[][] board, boolean redGo);

    /**
 * 根据FEN码获取棋谱数据
 * @param fenCode 棋局FEN码
 * @param onlyFinalPhase 是否只获取终局阶段的棋谱
 * @return 匹配的棋谱数据列表
 */
List<BookData> get(String fenCode, boolean onlyFinalPhase);

    /**
 * 关闭棋谱库资源
 */
void close();

    /**
 * 查询并排序棋谱数据
 * @param board 当前棋盘状态
 * @param redGo 是否轮到红方走棋
 * @param mr 走棋规则
 * @return 排序后的棋谱数据列表
 */
default List<BookData> query(char[][] board, boolean redGo, MoveRule mr) {
        List<BookData> list = get(board, redGo);
        sort(list, mr);
        return list;
    }

    /**
 * 根据FEN码查询并排序棋谱数据
 * @param fenCode 棋局FEN码
 * @param onlyFinalPhase 是否只获取终局阶段的棋谱
 * @param mr 走棋规则
 * @return 排序后的棋谱数据列表
 */
default List<BookData> query(String fenCode, boolean onlyFinalPhase, MoveRule mr) {
        List<BookData> list = get(fenCode, onlyFinalPhase);
        sort(list, mr);
        return list;
    }

    /**
 * 根据指定规则对棋谱数据进行排序
 * @param list 待排序的棋谱数据列表
 * @param mr 排序规则
 */
default void sort(List<BookData> list, MoveRule mr) {
        Collections.sort(list, new Comparator<BookData>() {
            private Random rd = new SecureRandom();
            @Override
            public int compare(BookData o1, BookData o2) {
                switch (mr) {
                    case BEST_SCORE: {
                        return o1.getScore() > o2.getScore() ? -1 : (o1.getScore() < o2.getScore() ? 1 : 0);
                    }
                    case BEST_WINRATE: {
                        return o1.getWinRate() > o2.getWinRate() ? -1 : (o1.getWinRate() < o2.getWinRate() ? 1 : 0);
                    }
                    case FULL_RANDOM: {
                        if (rd.nextInt(100) < 50) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                    case POSITIVE_RANDOM: {
                        if (o1.getScore() > 0 && o2.getScore() > 0) {
                            if (rd.nextInt(100) < 50) {
                                return 1;
                            } else {
                                return -1;
                            }
                        } else {
                            return o1.getScore() > o2.getScore() ? -1 : (o1.getScore() < o2.getScore() ? 1 : 0);
                        }
                    }
                    default: {
                        return 0;
                    }
                }
            }
        });
    }

}
