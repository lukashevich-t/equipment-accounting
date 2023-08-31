<template>
  <!-- 
  old: hHh lpR fFf
default: lHh Lpr lFf
 -->
  <q-layout view="hHh lpR fFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>Учет оборудования</q-toolbar-title>

        <!-- <div>Quasar v{{ $q.version }}</div> -->
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item clickable to="/">
          <q-item-section avatar>
            <q-icon name="search" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Поиск</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/create">
          <q-item-section avatar>
            <q-icon name="create_new_folder" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Создание</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/association">
          <q-item-section avatar>
            <q-icon name="move_to_inbox" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Объединение</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/test">
          <q-item-section>
            <q-item-label>Тест</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';

export default defineComponent({
  name: 'MainLayout',

  setup() {
    const leftDrawerOpen = ref(false);

    return {
      leftDrawerOpen,
      toggleLeftDrawer: () => (leftDrawerOpen.value = !leftDrawerOpen.value),
    };
  },
});
</script>
