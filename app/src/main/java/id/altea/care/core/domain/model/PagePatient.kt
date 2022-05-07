package id.altea.care.core.domain.model

data class PagePatient (
        val patients : List<PatientFamily>,
        val meta: MetaPage
)