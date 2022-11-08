package com.hdy.dbmovie.service;

import java.util.Set;

public interface GiveLikeService {
     boolean giveLike(String id,String userId);
     Long likedNo(String id);
     Set<Object> likedList(String id);

     boolean giveDisike(String id,String userId);
     Long dislikedNo(String id);
     Set<Object> dislikedList(String id);

}
