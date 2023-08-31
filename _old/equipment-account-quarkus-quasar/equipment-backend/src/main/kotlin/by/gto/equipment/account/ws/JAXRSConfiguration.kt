package by.gto.equipment.account.ws

import by.gto.equipment.account.model.CONTEXT_PATH
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationPath(CONTEXT_PATH)
open class JAXRSConfiguration : Application()
