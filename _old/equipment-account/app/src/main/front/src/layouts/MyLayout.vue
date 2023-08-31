<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          @click="leftDrawerOpen = !leftDrawerOpen"
          aria-label="Menu"
        >
          <q-icon name="menu" />
        </q-btn>

        <q-toolbar-title>Учет оборудования</q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      bordered
      content-class="bg-grey-2"
      :width="180"
    >
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
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { openURL } from 'quasar'

export default {
  name: 'MyLayout',
  data() {
    return {
      leftDrawerOpen: this.$q.platform.is.desktop,
    }
  },
  methods: {
    openURL,
  },
}
</script>

<style></style>
