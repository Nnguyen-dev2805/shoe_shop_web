package com.dev.shoeshop.scheduler;

import com.dev.shoeshop.entity.FlashSale;
import com.dev.shoeshop.enums.FlashSaleStatus;
import com.dev.shoeshop.repository.FlashSaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler tự động cập nhật status của Flash Sale
 * Chạy định kỳ mỗi phút để check và update status
 * 
 * ⚠️ Cần enable scheduling trong Application class:
 * @EnableScheduling
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FlashSaleScheduler {
    
    private final FlashSaleRepository flashSaleRepo;
    
    /**
     * Job tự động activate và end flash sales
     * 
     * Chạy: Mỗi 60 giây (1 phút)
     * Cron format: (giây) (phút) (giờ) (ngày) (tháng) (thứ)
     * 
     * Flow:
     * 1. Tìm flash sales có status=SCHEDULED và startTime <= now → Set ACTIVE
     * 2. Tìm flash sales có status=ACTIVE và endTime <= now → Set ENDED
     * 
     * VD Timeline:
     * - 11:59 → Flash sale status = SCHEDULED
     * - 12:00 → Scheduler chạy → Chuyển ACTIVE
     * - 14:00 → Scheduler chạy → Chuyển ENDED
     */
    @Scheduled(fixedRate = 60000) // 60,000 ms = 1 phút
    public void updateFlashSaleStatus() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Running flash sale status update job at {}", now);
        
        // PHẦN 1: ACTIVATE flash sales đã đến giờ
        activateScheduledFlashSales(now);
        
        // PHẦN 2: END flash sales đã hết giờ
        endActiveFlashSales(now);
    }
    
    /**
     * Activate các flash sales đã đến giờ
     * 
     * Logic:
     * - Tìm flash sales có status = SCHEDULED
     * - Có startTime <= now (đã đến giờ rồi)
     * - Chuyển status → ACTIVE
     * 
     * @param now Thời gian hiện tại
     */
    private void activateScheduledFlashSales(LocalDateTime now) {
        List<FlashSale> toActivate = flashSaleRepo
            .findByStatusAndStartTimeBeforeAndIsDeleteFalse(
                FlashSaleStatus.SCHEDULED,
                now
            );
        
        if (!toActivate.isEmpty()) {
            log.info("Activating {} flash sales", toActivate.size());
            
            for (FlashSale flashSale : toActivate) {
                flashSale.setStatus(FlashSaleStatus.ACTIVE);
                flashSaleRepo.save(flashSale);
                
                log.info("Flash sale {} '{}' is now ACTIVE", 
                         flashSale.getId(), flashSale.getName());
                
                // Optional: Gửi notification cho users
                // notificationService.sendFlashSaleStarted(flashSale);
            }
        }
    }
    
    /**
     * End các flash sales đã hết giờ
     * 
     * Logic:
     * - Tìm flash sales có status = ACTIVE
     * - Có endTime <= now (đã hết giờ rồi)
     * - Chuyển status → ENDED
     * 
     * @param now Thời gian hiện tại
     */
    private void endActiveFlashSales(LocalDateTime now) {
        List<FlashSale> toEnd = flashSaleRepo
            .findByStatusAndEndTimeBeforeAndIsDeleteFalse(
                FlashSaleStatus.ACTIVE,
                now
            );
        
        if (!toEnd.isEmpty()) {
            log.info("Ending {} flash sales", toEnd.size());
            
            for (FlashSale flashSale : toEnd) {
                flashSale.setStatus(FlashSaleStatus.ENDED);
                flashSaleRepo.save(flashSale);
                
                log.info("Flash sale {} '{}' has ENDED. Total sold: {}/{}", 
                         flashSale.getId(), 
                         flashSale.getName(),
                         flashSale.getTotalSold(),
                         flashSale.getTotalItems());
                
                // Optional: Gửi email báo cáo kết quả flash sale cho admin
                // emailService.sendFlashSaleReport(flashSale);
            }
        }
    }
}
