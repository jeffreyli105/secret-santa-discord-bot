package com.jeffrey.services;

import java.util.HashMap;
import java.util.Map;

public class TextService {
    public static final Map<String, Map<String,String>> MESSAGES = new HashMap<>();

    static {
        Map<String,String> en = new HashMap<>();
        en.put("q1_ask", "Language saved. Question 1: Your perfect winter evening:");
        en.put("q2_ask", "Question 2: Favourite holiday colour palette:");
        en.put("q3_ask", "Question 3: Best holiday treat:");
        en.put("q4_ask", "Question 4: Choose the Christmas animal you relate to most:");
        en.put("q1_placeholder", "Your perfect winter evening");
        en.put("q1_option1", "Baking cookies and watching movies");
        en.put("q1_option2", "Playing games with friends and family");
        en.put("q1_option3", "A big family dinner");
        en.put("q1_option4", "Out on the town — market, light walk, concert, or event");
        en.put("q2_placeholder", "Favourite holiday colour palette");
        en.put("q2_option1", "Classic — red & forest green");
        en.put("q2_option2", "Warm — golds, creams, cozy neutrals");
        en.put("q2_option3", "Frosty — icy blue & silver");
        en.put("q2_option4", "Deep — burgundy, plum & candlelit tones");
        en.put("q3_placeholder", "Best holiday treat");
        en.put("q3_option1", "Cookies (ginger, shortbread, sugar)");
        en.put("q3_option2", "Candy & chocolate (truffles, sweets)");
        en.put("q3_option3", "Savoury platter / cheese & charcuterie");
        en.put("q3_option4", "Something special from a local bakery or café");
        en.put("q4_placeholder", "Choose the Christmas animal you relate to most");
        en.put("q4_option1", "Reindeer — cheerful & helpful");
        en.put("q4_option2", "Penguin — playful & social");
        en.put("q4_option3", "Fox — clever & mischievous");
        en.put("q4_option4", "Polar Bear — cozy, big-hearted & generous");
        en.put("not_registered", "You are not registered. Use /register first.");
        en.put("incorrect_password", "Incorrect password.");
        en.put("reveal_nullid", "Assignments haven't been generated yet or you aren't assigned.");
        en.put("gifting_to", "You are gifting to");
        en.put("here_wishlist", "Here is their wishlist");
        en.put("registration_modal_title", "Complete Registration");
        en.put("name_label", "Full name or display name");
        en.put("password_label", "Password (≥6 chars)");
        en.put("email_label", "Email");
        en.put("wishlist_label", "Gift suggestions for your Santa to consider (up to 3)");
        en.put("thank_you", "Thanks — you're registered!");
        en.put("hi", "Hi");
        en.put("email_body", "Welcome to Secret Santa!\n"
                		+ "Your Secret Santa pairing is ready.\n"
                		+ "To reveal who you are gifting to: run /reveal in Discord and enter the password you used at registration.\n\n"
                		+ "Do NOT share your password with anyone.\n\n"
                		+ "- Secret Santa Bot 🎅");
        en.put("email_subject", "Secret Santa — how to reveal your assignment");
        // add more messages

        Map<String,String> zh = new HashMap<>();
        zh.put("q1_ask", "语言已保存。问题 1：您理想的冬日夜晚是什么样的?");
        zh.put("q2_ask", "问题 2：最喜欢的节日配色方案:");
        zh.put("q3_ask", "问题 3：最佳节日美食:");
        zh.put("q4_ask", "问题 4：选择你最认同的圣诞动物:");
        zh.put("q1_placeholder", "你完美的冬夜");
        zh.put("q1_option1", "烤饼干和看电影");
        zh.put("q1_option2", "和朋友家人一起玩游戏");
        zh.put("q1_option3", "一顿丰盛的家庭晚餐");
        zh.put("q1_option4", "外出逛逛——逛逛集市、散散步、听听音乐会或参加活动");
        zh.put("q2_placeholder", "最喜欢的节日配色方案");
        zh.put("q2_option1", "经典款——红色和森林绿");
        zh.put("q2_option2", "暖色调——金色、米色、温馨的中性色");
        zh.put("q2_option3", "霜冻——冰蓝色和银色");
        zh.put("q2_option4", "深邃的酒红色、李子色和烛光色调");
        zh.put("q3_placeholder", "最好的节日款待");
        zh.put("q3_option1", "饼干（姜饼、酥饼、糖饼干)");
        zh.put("q3_option2", "糖果和巧克力（松露巧克力、甜点)");
        zh.put("q3_option3", "咸味拼盘/奶酪和熟食");
        zh.put("q3_option4", "来自当地面包店或咖啡馆的特色美食");
        zh.put("q4_placeholder", "选择你最有共鸣的圣诞动物.");
        zh.put("q4_option1", "驯鹿——快乐又乐于助人");
        zh.put("q4_option2", "企鹅——活泼好动，喜欢社交");
        zh.put("q4_option3", "狐狸——聪明又调皮");
        zh.put("q4_option4", "北极熊——温暖、善良、慷慨");
        zh.put("not_registered", "您尚未注册。请先使用 /register 命令注册.");
        zh.put("incorrect_password", "密码错误.");
        zh.put("reveal_nullid", "任务尚未生成，或者您尚未被分配任务.");
        zh.put("gifting_to", "您正在赠送给");
        zh.put("here_wishlist", "这是他们的愿望清单");
        zh.put("registration_modal_title", "完成注册");
        zh.put("name_label", "全名或显示名称");
        zh.put("password_label", "密码（≥6 个字符）");
        zh.put("email_label", "电子邮件");
        zh.put("wishlist_label", "你最想要的礼物是什么（最多3个）");
        zh.put("thank_you", "谢谢 — 您已注册！");
        zh.put("hi", "你好");
        zh.put("email_body", "欢迎参加秘密圣诞老人活动!\n"
        		+ "您的秘密圣诞老人配对已准备就绪.\n"
        		+ "要查看您的礼物赠送对象，请在 Discord 中运行 /reveal 命令并输入您注册时使用的密码.\n\n"
        		+ "请勿与任何人分享您的密码.\n\n"
        		+ "- 秘密圣诞老人机器人 🎅");
        zh.put("email_subject", "秘密圣诞老人——如何揭晓你的任务");
        // add more messages

        MESSAGES.put("english", en);
        MESSAGES.put("mandarin", zh);
    }

    public static String get(String lang, String key) {
        Map<String,String> m = MESSAGES.getOrDefault(lang, MESSAGES.get("english"));
        return m.getOrDefault(key, key);
    }
}

