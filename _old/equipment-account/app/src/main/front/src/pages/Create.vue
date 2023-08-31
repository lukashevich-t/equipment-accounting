<template>
  <div>
    <form action="s/eq/gen_inv" method="GET" target="_blank" @submit.prevent="submit" ref="form">
        <input type="hidden" name="date" ref="date">
        <input type="hidden" name="templateFilename" ref="templateFilename">
        <q-input outlined v-model="date" label="Дата покупки" dense>
        <template v-slot:append>
            <q-icon name="event" class="cursor-pointer">
            <q-popup-proxy ref="qDateProxy" transition-show="scale" transition-hide="scale">
                <q-date v-model="date" @input="() => $refs.qDateProxy.hide()" mask="DD.MM.YYYY" :locale="locale"/>
            </q-popup-proxy>
            </q-icon>
        </template>
        </q-input>
        <q-input dense outlined v-model="serial" label="Инвентарник" placeholder="Пример: 0019123*5,480100,480110" name="range"/>
        <q-option-group dense
            v-model="templateFilename"
            color="primary"
            :options="[ { label: '6x6', value: 'a4_6x6.jasper' }, { label: '12x12', value: 'a4_12x12.jasper' }]"
            />
        <q-btn @click="submit" :disabled="$v.$invalid">Создать</q-btn>
    </form>
  </div>
</template>

<script>
import { required } from 'vuelidate/lib/validators'
import { regex } from 'src/lib/validators'
import { locale } from 'src/lib/consts'
import { dateDisplayToIso } from 'src/lib/funcs'
export default {
  data() {
    return {
      date: null,
      serial: null,
      templateFilename: 'a4_6x6.jasper',
      locale,
    }
  },
  validations() {
    return {
      date: { regex: regex(/^\d{2}\.\d{2}\.\d{4}$/) },
      serial: { required },
    }
  },

  methods: {
    submit() {
      let d = dateDisplayToIso(this.date)
      console.log('parsed date ', d)
      this.$refs.date.value = d
      this.$refs.templateFilename.value = this.templateFilename
      console.log('this.$refs.date ', this.$refs.date)
      console.log('this.$refs.form ', this.$refs.form)
      this.$refs.form.submit()
    }
  }
}
</script>

<style>
</style>
