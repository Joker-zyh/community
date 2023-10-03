package com.henu.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器：
 *      1. 创建敏感词文件
 *      2. 创建敏感词过滤器类
 *      3. 创建前缀树
 *      4. 加载敏感词文件，将敏感词添加到前缀树中
 *      5. 创建过滤方法。
 */

@Component
public class SensitiveFilter {
    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //前缀树根节点
    private TrieNode root = new TrieNode();

    //敏感词替换
    private static final String REPLACE_WORD = "***";


    // 在Spring容器初始化时执行该方法
    @PostConstruct
    private void init(){
        try(
                //创建输入流，读取文件内容
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        ){
            String buffer;
            //读取文件每一行
            while ((buffer = bf.readLine()) != null){
                //将敏感词加入前缀树中
                TrieNode cur = root;
                for (int i = 0; i < buffer.length(); i++) {
                    char c = buffer.charAt(i);
                    TrieNode subNode = cur.getSubNode(c);
                    //判断该词是否在前缀树中，若在则跳过，不在就加入
                    if (subNode == null){
                        subNode = new TrieNode();
                        cur.setSubNode(c,subNode);
                    }
                    //到此处，subNode都是有值的
                    cur = subNode;
                    //将末尾节点标记
                    if (i == buffer.length() - 1){
                        cur.setEnd(true);
                    }
                }
            }
        }catch (IOException e){
            logger.error("读取敏感词文件失败：" + e.getMessage());
        }
    }

    /**
     * 过滤敏感词
     * @param text 传入的文本
     * @return 返回过滤后的文本
     */
    public String filter(String text){
        //判断文本是否为空
        if (StringUtils.isBlank(text)){
            return null;
        }
        //定义三个指针变量，遍历text文本
        TrieNode cur = root;
        int begin = 0;
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        //遍历文本
        while (begin < text.length()){
            //获取词
            char c = text.charAt(position);

            //判断是否为符号
            if (isSymbol(c)){
                if (cur == root){
                    sb.append(c);
                    begin++;
                }
                position++;
                //如果position超过文本长度，说明以begin开头的字符串不违规
                if (position >= text.length() && begin < text.length()){
                    sb.append(text.charAt(begin));
                    begin++;
                    position = begin;
                    //返回根节点
                    cur = root;
                }
                continue;
            }

            //不是符号，就要判断字符是否为敏感词
            cur = cur.getSubNode(c);
            if (cur == null){
                //不是敏感词
                sb.append(c);
                begin++;
                position++;
                //返回根节点
                cur = root;
            }else if (cur.isEnd()){
                //是敏感词，并且已经到结尾
                sb.append(REPLACE_WORD);
                position+=1;
                begin = position;
                //返回根节点
                cur = root;
            }else {
                position++;
            }
            //如果position超过文本长度，说明以begin开头的字符串不违规
            if (position >= text.length() && begin < text.length()){
                sb.append(text.charAt(begin));
                begin++;
                position = begin;
                //返回根节点
                cur = root;
            }
        }
        return sb.toString();
    }


    private boolean isSymbol(char c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF );//不是字符 && 东亚文字范围
    }

    //匿名内部类
    private class TrieNode{
        //存储的子节点及子节点的值
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //是否为前缀树的尾结点
        private boolean isEnd = false;

        // 存储子节点
        public void setSubNode(char c,TrieNode trieNode){
            subNodes.put(c,trieNode);
        }

        //获取子节点
        public TrieNode getSubNode(char c){
            return subNodes.get(c);
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }
    }

}
