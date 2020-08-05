import axios from "axios";
import http from ".././utils/http";
import config from "../config";

// TODO: change URL when in prod
const API_URL = `${config.baseUrl}/`

/**
 * Implements user login/logout methods. TODO: add a registration page.
 */
class AuthService {
    login(user) {
        return http.post(API_URL + "auth", user).then((response) => {
            let user = response.data;
            let token = response.headers.authorization;
            console.log(token);
            if (token) {
                user.token = token;
                localStorage.setItem("user", JSON.stringify(user));
            }
            return user;
        });
    }

    loginGuest() {
        return http.post(API_URL + "auth/guest").then((response) => {
            let user = response.data;
            let token = response.headers.authorization;
            console.log(token);
            if (token) {
                user.token = token;
                localStorage.setItem("user", JSON.stringify(user));
            }
            return user;
        });
    }

    logout() {
        localStorage.removeItem("user");
    }

    // unused for now, but may be useful later
    register(user) {
        return axios.post(API_URL + "register", {
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            password: user.password,
            role: user.role,
        });
    }
}

export default new AuthService();
