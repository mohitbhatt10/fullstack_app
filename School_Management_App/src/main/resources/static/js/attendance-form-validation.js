// Handle attendance form validation and time window display
document.addEventListener('DOMContentLoaded', function() {
    const courseSelect = document.getElementById('courseSelect');
    const attendanceDate = document.getElementById('attendanceDate');
    const attendanceForm = document.getElementById('attendanceForm');
    const timeWindowInfo = document.getElementById('timeWindowInfo');

    function updateTimeWindow() {
        const courseId = courseSelect.value;
        const date = attendanceDate.value;

        if (!courseId || !date) {
            return;
        }

        // Fetch the time window for the selected course and date
        fetch(`/api/attendance/time-window?courseId=${courseId}&date=${date}`)
            .then(response => response.json())
            .then(timeWindow => {
                if (!timeWindow) {
                    timeWindowInfo.innerHTML = '<div class="alert alert-warning">No class scheduled for this date</div>';
                    attendanceForm && (attendanceForm.style.display = 'none');
                    return;
                }

                const now = new Date();
                const currentTime = now.getHours().toString().padStart(2, '0') + ':' + 
                                  now.getMinutes().toString().padStart(2, '0');

                let alertClass = 'alert-info';
                let message = '';

                // For current date
                if (date === now.toISOString().split('T')[0]) {
                    const windowStart = timeWindow.startTime;
                    const windowEnd = timeWindow.endTime;

                    if (currentTime < windowStart) {
                        alertClass = 'alert-warning';
                        message = `Too early to mark attendance. You can mark attendance from ${windowStart}`;
                    } else if (currentTime > windowEnd) {
                        alertClass = 'alert-danger';
                        message = `Attendance window closed at ${windowEnd}`;
                    } else {
                        alertClass = 'alert-success';
                        message = `Attendance window is open until ${windowEnd}`;
                    }
                }
                // For past dates
                else if (date < now.toISOString().split('T')[0]) {
                    alertClass = 'alert-info';
                    message = `Class scheduled from ${timeWindow.classStartTime} to ${timeWindow.classEndTime}`;
                }
                // For future dates
                else {
                    alertClass = 'alert-warning';
                    message = 'Cannot mark attendance for future dates';
                    attendanceForm && (attendanceForm.style.display = 'none');
                }

                timeWindowInfo.innerHTML = `<div class="alert ${alertClass}">${message}</div>`;
            })
            .catch(error => {
                console.error('Error fetching time window:', error);
                timeWindowInfo.innerHTML = '<div class="alert alert-danger">Error checking schedule</div>';
            });
    }

    // Add event listeners
    if (courseSelect) {
        courseSelect.addEventListener('change', updateTimeWindow);
    }
    if (attendanceDate) {
        attendanceDate.addEventListener('change', updateTimeWindow);
    }

    // Initialize on page load if course is selected
    if (courseSelect && courseSelect.value) {
        updateTimeWindow();
    }
});
