package com.github.mille.team.entity;

import com.github.mille.team.Player;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public interface EntityOwnable {

    String getOwnerName();

    void setOwnerName(String playerName);

    Player getOwner();

}
