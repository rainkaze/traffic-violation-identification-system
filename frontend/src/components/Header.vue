<template>
  <header class="bg-white shadow-sm z-10">
    <div class="flex items-center justify-between p-4">
      <div class="flex items-center">
        <button @click="toggleSidebar" class="md:hidden mr-4 text-gray-500 hover:text-primary">
          <i class="fa fa-bars text-xl"></i>
        </button>
        <div class="relative">
          <input type="text" placeholder="搜索..." class="input pl-10 pr-4 w-64" />
          <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <button class="relative text-gray-500 hover:text-primary">
          <i class="fa fa-bell text-xl"></i>
          <span
            class="absolute -top-1 -right-1 bg-danger text-white text-xs rounded-full w-4 h-4 flex items-center justify-center"
          >5</span
          >
        </button>
        <div class="relative group">
          <button class="flex items-center gap-2 text-gray-700 hover:text-primary">
            <img
              src="https://picsum.photos/id/1005/200/200"
              alt="用户头像"
              class="w-8 h-8 rounded-full object-cover border-2 border-primary"
            />
            <span class="hidden md:inline">克拉玛依交警</span>
            <i class="fa fa-angle-down"></i>
          </button>
          <div
            class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 z-50 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 transform group-hover:translate-y-0 translate-y-2"
          >
            <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">
              <i class="fa fa-user mr-2"></i>个人信息
            </a>
            <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">
              <i class="fa fa-cog mr-2"></i>账号设置
            </a>
            <div class="border-t border-gray-200 my-1"></div>
            <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100" @click.prevent="logout">
              <i class="fa fa-sign-out mr-2"></i>退出登录
            </a>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
defineProps({
  toggleSidebar: Function,
});

const logout = () => {
  if (confirm('确定要退出登录吗？')) {
    alert('已成功退出登录');
  }
};
</script>
