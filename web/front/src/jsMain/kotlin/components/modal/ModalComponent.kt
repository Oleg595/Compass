package components.modal

import csstype.ClassName
import react.ComponentClass
import react.Props

external interface ModalProps: Props {
    var isOpen: Boolean
    var onRequestClose: () -> Unit
    var className: ClassName
    var contentLabel: String
}

@JsModule("react-modal")
@JsNonModule
external val Modal: ComponentClass<ModalProps>
