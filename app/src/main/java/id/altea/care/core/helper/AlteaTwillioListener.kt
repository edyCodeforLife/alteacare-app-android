package id.altea.care.core.helper

import com.twilio.chat.*
import com.twilio.video.*
import timber.log.Timber

object AlteaTwillioListener {
    interface RoomListener : Room.Listener {
        override fun onConnected(room: Room) {

        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {

        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {

        }

        override fun onReconnected(room: Room) {

        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {

        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {

        }

        override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {

        }

        override fun onParticipantReconnecting(room: Room, remoteParticipant: RemoteParticipant) {

        }

        override fun onParticipantReconnected(room: Room, remoteParticipant: RemoteParticipant) {

        }

        override fun onDominantSpeakerChanged(room: Room, remoteParticipant: RemoteParticipant?) {

        }

        override fun onRecordingStarted(room: Room) {
        }

        override fun onRecordingStopped(room: Room) {
        }


    }

    interface RemoteParticipantListener : RemoteParticipant.Listener {

        override fun onAudioTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {

        }

        override fun onAudioTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {

        }

        override fun onAudioTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {

        }

        override fun onAudioTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            twilioException: TwilioException
        ) {

        }

        override fun onAudioTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            remoteAudioTrack: RemoteAudioTrack
        ) {

        }

        override fun onAudioTrackPublishPriorityChanged(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication,
            trackPriority: TrackPriority
        ) {

        }

        override fun onVideoTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {

        }

        override fun onVideoTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {

        }

        override fun onVideoTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {

        }

        override fun onVideoTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            twilioException: TwilioException
        ) {

        }

        override fun onVideoTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            remoteVideoTrack: RemoteVideoTrack
        ) {

        }

        override fun onVideoTrackPublishPriorityChanged(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication,
            trackPriority: TrackPriority
        ) {

        }

        override fun onDataTrackPublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {

        }

        override fun onDataTrackUnpublished(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication
        ) {

        }

        override fun onDataTrackSubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {

        }

        override fun onDataTrackSubscriptionFailed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            twilioException: TwilioException
        ) {

        }

        override fun onDataTrackUnsubscribed(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            remoteDataTrack: RemoteDataTrack
        ) {

        }

        override fun onDataTrackPublishPriorityChanged(
            remoteParticipant: RemoteParticipant,
            remoteDataTrackPublication: RemoteDataTrackPublication,
            trackPriority: TrackPriority
        ) {

        }

        override fun onAudioTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {

        }

        override fun onAudioTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteAudioTrackPublication: RemoteAudioTrackPublication
        ) {

        }

        override fun onVideoTrackEnabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {

        }

        override fun onVideoTrackDisabled(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrackPublication: RemoteVideoTrackPublication
        ) {

        }

        override fun onVideoTrackSwitchedOn(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrack: RemoteVideoTrack
        ) {

        }

        override fun onVideoTrackSwitchedOff(
            remoteParticipant: RemoteParticipant,
            remoteVideoTrack: RemoteVideoTrack
        ) {

        }

        override fun onNetworkQualityLevelChanged(
            remoteParticipant: RemoteParticipant,
            networkQualityLevel: NetworkQualityLevel
        ) {

        }
    }

    interface ChatListener : ChatClientListener {
        override fun onChannelJoined(channel: Channel?) {

        }

        override fun onChannelInvited(channel: Channel?) {

        }

        override fun onChannelAdded(channel: Channel?) {

        }

        override fun onChannelUpdated(channel: Channel?, p1: Channel.UpdateReason?) {

        }

        override fun onChannelDeleted(channel: Channel?) {

        }

        override fun onChannelSynchronizationChange(p0: Channel?) {

        }

        override fun onError(p0: ErrorInfo?) {

        }

        override fun onUserUpdated(p0: User?, p1: User.UpdateReason?) {

        }

        override fun onUserSubscribed(p0: User?) {

        }

        override fun onUserUnsubscribed(p0: User?) {

        }

        override fun onClientSynchronization(p0: ChatClient.SynchronizationStatus?) {

        }

        override fun onNewMessageNotification(p0: String?, p1: String?, p2: Long) {

        }

        override fun onAddedToChannelNotification(p0: String?) {

        }

        override fun onInvitedToChannelNotification(p0: String?) {

        }

        override fun onRemovedFromChannelNotification(p0: String?) {

        }

        override fun onNotificationSubscribed() {

        }

        override fun onNotificationFailed(p0: ErrorInfo?) {

        }

        override fun onConnectionStateChange(p0: ChatClient.ConnectionState?) {

        }

        override fun onTokenExpired() {

        }

        override fun onTokenAboutToExpire() {

        }
    }

    interface ChannelListener : com.twilio.chat.ChannelListener {
        override fun onMessageAdded(p0: Message?) {

        }

        override fun onMessageUpdated(p0: Message?, p1: Message.UpdateReason?) {

        }

        override fun onMessageDeleted(p0: Message?) {

        }

        override fun onMemberAdded(p0: Member?) {

        }

        override fun onMemberUpdated(p0: Member?, p1: Member.UpdateReason?) {

        }

        override fun onMemberDeleted(p0: Member?) {

        }

        override fun onTypingStarted(p0: Channel?, p1: Member?) {

        }

        override fun onTypingEnded(p0: Channel?, p1: Member?) {

        }

        override fun onSynchronizationChanged(p0: Channel?) {

        }
    }
}


