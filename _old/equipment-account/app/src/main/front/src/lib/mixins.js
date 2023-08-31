export const showError = {
  methods: {
    $showError(msg, err) {
      let vm = this
      vm.$log.error(err)
      if (err.errCode > 0) {
        vm.$q.dialog({
          title: 'Error',
          message: `${msg}: ${err.message}`,
          persistent: true,
        })
      } else {
        vm.$q.dialog({
          title: 'Error',
          message: msg || 'Системная ошибка. Обратитесь в поддержку.',
          persistent: true,
        })
      }
    },
  },
}
