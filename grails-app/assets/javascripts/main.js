/**
 * Main jQuery-function for layout-specific-js
 */
$(function() {

    checkNotifications();

    //  Handle error box closings
    $(".js-notification").on('closed.bs.alert', function() {
        hideOverlay();
    });
});

/**
 * Handles showing of the error, warning and success alerts.
 *
 * This function only handles messages that are given as flash-messages.
 *
 */
function checkNotifications() {

    var error = $(".js-error-message").text();
    var warning = $(".js-warning-message").text();
    var success = $(".js-success-message").text();

    if (error !== "") {
        $(".js-error").show();
        showOverlay();
    }

    else if (warning !== "") {
        $(".js-warning").show();
        showOverlay();
    }

    else if (success !== "") {
        $(".js-success").show();
        showOverlay();
    }
}

function showOverlay() {
    $(".js-overlay").show();
}

function hideOverlay() {
    $(".js-overlay").hide();
}