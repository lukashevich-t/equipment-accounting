<template>
  <div>
    <q-card>
      <q-card-section class="row items-center">
        <div class="text-h6">
          {{ equipment.inv_number }} {{ equipment.type }}
        </div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>

      <q-card-section class="items-center">
        <q-splitter v-model="splitterModel" style="height: 400px">
          <template v-slot:before>
            <div
              v-for="(e, i) in entries"
              :key="i"
              class="q-my-sm"
              :class="{ selected: i == selectedIndex }"
              @click="select(i, e)"
              style="cursor: pointer;"
            >
              {{ e.time }}
            </div>
          </template>

          <template v-slot:after>
            <div class="q-my-md" v-html="selectedText"></div>
          </template>
        </q-splitter>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn icon="close" label="Закрыть" color="primary" @click="close" />
      </q-card-actions>
    </q-card>
  </div>
</template>

<script>
// import { mapGetters } from 'vuex'
import { ajax } from 'src/lib/api'
import { showError } from 'src/lib/mixins'
export default {
  data() {
    return {
      splitterModel: 30,
      entries: [],
      selectedIndex: -1,
      selectedText: null,
    }
  },
  props: {
    guid: { type: String, required: true },
    equipment: { type: Object, required: true },
  },
  mixins: [showError],
  methods: {
    close() {
      this.$emit('close')
    },
    select(i) {
      let vm = this
      vm.selectedIndex = i
      let e = vm.entries[i]
      let text = `Тип: ${e.actionName}\nПользователь: ${e.userName}\n\nСодержание: ${e.comment}`
      vm.selectedText = text.replace(new RegExp('\\n', 'g'), '<br>')
    },
  },
  mounted() {
    let vm = this
    vm.selectedIndex = -1
    vm.selectedText = null
    ajax
      .getEqLog({ guid: vm.guid })
      .then(
        r => {
          vm.entries = r.data.content.entries
          vm.$log.debug(vm.entries)
          if (vm.entries.length) {
            vm.select(0)
          }
        },
        err => {
          vm.$showError('Ошибка', err)
        }
      )
      .finally(() => {
        vm.blockUI = false
      })
  },
}
</script>
<style scoped>
.selected {
  background-color: antiquewhite;
}
</style>
