package financial.controllers;

class UserHeader {
    static Long getOrDefault(Long header){
        return header != null ? header : 1L;
    }
}
