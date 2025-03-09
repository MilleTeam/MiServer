package com.github.mille.team.network.protocol;

import com.github.mille.team.utils.RuleData;

/**
 * author: MagicDroidX Nukkit Project
 */
public class GameRulesChangedPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.GAME_RULES_CHANGED_PACKET;

    public RuleData[] ruleDatas = new RuleData[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.ruleDatas.length);
        for (RuleData rule : this.ruleDatas) {
            this.putRuleData(rule);
        }
    }

}
