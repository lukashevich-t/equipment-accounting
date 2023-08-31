<template>
  <div id="q-app">
    <router-view />
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ajax } from 'src/lib/api'
export default {
  name: 'App',
  computed: { ...mapGetters(['eqStates', 'eqTypes', 'persons']) },
  methods: {
    ...mapActions(['setEqTypes', 'setEqStates', 'setPersons']),
    prepareRef(ref) {
      for (let i in ref) {
        let e = ref[i]
        e.value = e.id
        e.label = e.name
        delete e.name
        delete e.id
      }
      return ref.sort((a, b) => {
        return a.label.localeCompare(b.label)
      })
    },
  },
  mounted() {
    let vm = this
    let loaders = [
      { loader: ajax.getEqStates, name: 'eqStates', actionName: 'setEqStates' },
      { loader: ajax.getEqTypes, name: 'eqTypes', actionName: 'setEqTypes' },
      { loader: ajax.getPersons, name: 'persons', actionName: 'setPersons' },
    ]
    for (let i in loaders) {
      let l = loaders[i]
      l.loader().then(
        r => {
          vm[l.actionName](vm.prepareRef(r.data.content.data))
        },
        err => {
          vm.$log.error(`error loading ${l.name} `, err)
        }
      )
    }
  },
}
</script>

<style>
</style>
