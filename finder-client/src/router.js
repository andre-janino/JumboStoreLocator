import Vue from "vue";
import Router from "vue-router";
import LoginPage from "./components/views/Login";
import MainPage from "./components/views/MainPage";
import Profile from "./components/views/Profile";

Vue.use(Router);

const router = new Router({
  routes: [
    {
      path: "/",
      component: MainPage
    },
    {
      path: "/login",
      component: LoginPage
    },
    {
      path: "/profile",
      component: Profile
    },
  ],
  mode: "history"
});

router.beforeEach((to, from, next) => {
  const publicPages = ['/login'];
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem('user');

  // trying to access a restricted page + not logged in
  // redirect to login page
  if (authRequired && !loggedIn) {
    next('/login');
  } else {
    next();
  }
});

// export
export default  router;