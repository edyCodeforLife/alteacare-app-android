package id.altea.care.core.mappers

import id.altea.care.core.data.response.PromotionListGroupResponse
import id.altea.care.core.data.response.PromotionListResponse
import id.altea.care.core.data.response.WidgetsResponse
import id.altea.care.core.domain.model.*

object Mapper {

    fun Doctor?.mapToConsultation(doctorSchedule : DoctorSchedule? = null) : Consultation {
        val doctor = this
        val hospital = doctor?.hospital.orEmpty()
        val price: Price?
        val strikePrice: Price?

        if ((doctor?.flatPrice?.raw ?: 0) > 0 && (doctor?.price?.raw ?: 0) != 0L) {
            price = doctor?.flatPrice
            strikePrice = doctor?.price
        } else {
            price = doctor?.price
            strikePrice = doctor?.flatPrice
        }
        return Consultation(
            hospitalId = if (hospital.isEmpty()) "" else hospital[0].id.orEmpty(),
            iconRs = if (hospital.isEmpty()) "" else hospital[0].icon.orEmpty(),
            nameRs = if (hospital.isEmpty()) "" else hospital[0].name.orEmpty(),
            idDoctor = doctor?.doctorId.orEmpty(),
            imgDoctor = doctor?.photo.orEmpty(),
            priceDoctor = price?.formatted ?: "Rp 0",
            nameDoctor = doctor?.doctorName.orEmpty(),
            specialistDoctor = doctor?.specialization?.name.orEmpty(),
            codeSchedule = doctorSchedule?.code.orEmpty(),
            dateSchedule = doctorSchedule?.date.orEmpty(),
            startTime = doctorSchedule?.startTime.orEmpty(),
            endTime = doctorSchedule?.endTime.orEmpty(),
            priceDoctorDecimal = (price?.raw ?: 0).toLong(),
            priceStrikeDecimal = (strikePrice?.raw ?: 0).toLong(),
            priceStrike = strikePrice?.formatted ?: "Rp 0",
            specializationId = doctor?.specialization?.specializationId.orEmpty(),
        )
    }

    fun WidgetsResponse.mapToWidgets() : Widgets {
        return Widgets(
            id = id.orEmpty(),
            title = title.orEmpty(),
            icon = android?.icon?.url.orEmpty(),
            deepLinkType = android?.deeplinkType.orEmpty(),
            deeplinkUrl = android?.deeplinkUrl.orEmpty(),
            needLogin = needLogin
        )
    }

    fun PromotionListResponse.mapToPromotionList() : PromotionList {
        return PromotionList(
            promotionId = id ?: 0,
            status = status.isNullOrEmpty(),
            title =  title.orEmpty()  ,
            promotionType = promotionType.orEmpty(),
            weight = weight ?: 0,
            image = imageBannerThumbnail?.formats?.thumbnail.orEmpty()
        )
    }

    fun PromotionListGroupResponse.mapToPromotionListGroup() : PromotionListGroup {
        return  PromotionListGroup(
            promotionId = promotionTypeId.orEmpty(),
            promotionTitle = promotionType.orEmpty(),
            promotionList = promotionList?.map { listPromotion -> listPromotion?.mapToItemPromotionListGroup() }
        )
    }

    private fun PromotionListGroupResponse.Promotion.mapToItemPromotionListGroup() : ItemPromotionListGroup {
        return ItemPromotionListGroup(
            id = id ?: 0,
            weight = weight ?: 0,
            image = imageBannerThumbnail?.formats?.thumbnail.orEmpty(),
            promotionType =  promotionType.orEmpty(),
        )
    }
}