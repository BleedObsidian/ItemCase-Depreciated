package com.gmail.bleedobsidian.itemcase.util.tellraw;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONChatMessage {
    private ArrayList<JSONObject> chatObjects = new ArrayList<JSONObject>();

    public void addText(String text, JSONChatColor color,
            List<JSONChatFormat> formats) {
        JSONObject chatObject = new JSONObject();
        chatObject.put("text", text);

        if (color != null) {
            chatObject.put("color", color.getColorString());
        }

        if (formats != null) {
            for (JSONChatFormat format : formats) {
                chatObject.put(format.getFormatString(), true);
            }
        }

        this.chatObjects.add(chatObject);
    }

    public void addExtra(JSONChatExtra extraObject) {
        JSONObject chatObject = new JSONObject();

        chatObject.put("text", "");
        chatObject.put("color", "");

        if (!chatObject.containsKey("extra")) {
            chatObject.put("extra", new JSONArray());
        }
        JSONArray extra = (JSONArray) chatObject.get("extra");
        extra.add(extraObject.toJSON());
        chatObject.put("extra", extra);

        this.chatObjects.add(chatObject);
    }

    public void sendToPlayer(Player player) {
        // Bukkit.getLogger().info(chatObject.toJSONString());
        // Packet3Chat packet = new Packet3Chat(chatObject.toJSONString(),
        // true);
        // ((CraftPlayer)
        // player).getHandle().playerConnection.sendPacket(packet);

        ((CraftPlayer) player).getHandle().playerConnection
                .sendPacket(new PacketPlayOutChat(ChatSerializer.a(this
                        .toString()), true));

    }

    public String toString() {
        String json = "[";
        for (int i = 0; i < this.chatObjects.size(); i++) {
            JSONObject object = this.chatObjects.get(i);

            if (i == (this.chatObjects.size() - 1)) {
                json += object.toJSONString();
            } else {
                json += object.toJSONString() + ",";
            }
        }
        json += "]";

        return json;
    }
}
