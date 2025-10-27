package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.UserRankResponse;
import backend_for_react.backend_for_react.model.UserRank;

public class UserRankMapper {
    public static UserRankResponse toUserRankResponse(UserRank userRank) {
        if(userRank == null) {
            return null;
        }
        return UserRankResponse.builder()
                .id(userRank.getId())
                .name(userRank.getName())
                .minSpent(userRank.getMinSpent())
                .status(userRank.getStatus())
                .build();
    }
}
