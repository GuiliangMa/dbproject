import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/ManageView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Manage',
    component:()=>import("../views/ManageView.vue"),
    redirect:"/login",
    children:[
      {
        path:'home',
        name:'Home',
        component:()=>import("../views/Home.vue")
      },
      {
        path:'user',
        name:'User',
        component:()=>import("../views/User.vue")
      },
      {
        path: 'student',
        name:'Student',
        component:()=>import("../views/Student.vue")
      },
      {
        path: 'teacher',
        name:'Teacher',
        component:()=>import("../views/Teacher.vue")
      },
      {
        path: 'classroom',
        name:'Classroom',
        component:()=>import("../views/Classroom.vue")
      },
      {
        path: 'usagetable',
        name: 'UsageTable',
        component:()=>import("../views/UsageTable.vue")
      },
      {
        path: 'apply',
        name:'Apply',
        component:()=>import("../views/Apply.vue")
      },
      {
        path: 'free',
        name:'Free',
        component:()=>import("../views/Freeroom.vue")
      },
      {
        path: 'myapply',
        name: 'MyApply',
        component:()=>import("../views/MyApply.vue")
      },
      {
        path:'myusage',
        name:'MyUsage',
        component:()=>import("../views/MyUsage.vue")
      },
      {
        path:'course',
        name:'Course',
        component:()=>import("../views/Course.vue")
      },
      {
        path: 'teachercourse',
        name:'TeacherCourse',
        component:()=>import("../views/TeacherCourse.vue")
      },
      {
        path: 'teachercourseapply',
        name: 'TeacherCourseApply',
        component:()=>import("../views/TeacherCourseApply.vue")
      },
      {
        path: 'teachercoursetrial',
        name: 'TeacherCourseTrial',
        component:()=>import("../views/TeacherCourseTrial.vue")
      },
      {
        path: 'myteachercourse',
        name: 'MyTeacherCourse',
        component:()=>import("../views/MyTeacherCourse.vue")
      },
      {
        path: 'mycoursetrial',
        name: 'MyCourseTrial',
        component:()=>import("../views/MyCourseTrial.vue")
      },
      {
        path: 'studentcourse',
        name: 'StudentCourse',
        component:()=>import("../views/StudentCourse.vue")
      },
      {
        path: 'mycourse',
        name: 'MyCourse',
        component:()=>import("../views/MyCourse.vue")
      },
      {
        path: 'teach',
        name: 'Teach',
        component:()=>import("../views/Teach.vue")
      },
      {
        path: 'myasign',
        name: 'MyAsign',
        component:()=>import("../views/MyAsign.vue")
      },
      {
        path: 'assign',
        name:'Assign',
        component:()=>import("../views/AssignTable.vue")
      }
    ]
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path:'/login',
    name:'Login',
    component:()=>import("../views/Login.vue")
  },
  {
    path:'/register',
    name:'Register',
    component:()=>import("../views/Register.vue")
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
