/**
 * Provides the needed header to provide authentication info (access token) to requests
 */
export default function authHeader() {
    let user = JSON.parse(localStorage.getItem('user'));
  
    if (user && user.token) {
        return { 'x-access-token': user.token };
    } else {
        return {};
    }
}