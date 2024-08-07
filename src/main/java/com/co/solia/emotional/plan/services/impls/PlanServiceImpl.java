package com.co.solia.emotional.plan.services.impls;

import com.co.solia.emotional.plan.models.daos.PlanDao;
import com.co.solia.emotional.plan.models.dtos.rq.CreatePlanRqDto;
import com.co.solia.emotional.plan.models.dtos.rq.UpdatePlanRqDto;
import com.co.solia.emotional.plan.models.dtos.rs.CreatePlanRsDto;
import com.co.solia.emotional.plan.models.dtos.rs.GetPlanRsDto;
import com.co.solia.emotional.plan.models.dtos.rs.UpdatePlanRsDto;
import com.co.solia.emotional.plan.models.mappers.PlanMapper;
import com.co.solia.emotional.plan.models.repos.PlanRepo;
import com.co.solia.emotional.plan.services.services.PlanService;
import com.co.solia.emotional.share.models.dtos.rs.DefaultRsDto;
import com.co.solia.emotional.share.models.exceptions.CreatedException;
import com.co.solia.emotional.share.models.exceptions.NotFoundException;
import com.co.solia.emotional.share.utils.validators.ServiceValidator;
import com.co.solia.emotional.share.utils.validators.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * implementation of {@link PlanService}.
 * @author luis.bolivar.
 */
@Service
@Slf4j
@AllArgsConstructor
public class PlanServiceImpl implements PlanService {

    /**
     * dependency on {@link PlanRepo}.
     */
    private PlanRepo planRepo;

    /**
     * create a new plan.
     *
     * @param rq to create a plan.
     * @param adminCode permission to create plan.
     * @return {@link Optional} of {@link CreatePlanRsDto}.
     */
    @Override
    public Optional<CreatePlanRsDto> create(final CreatePlanRqDto rq, final String adminCode) {
        validateCreate(rq, adminCode);
        return PlanMapper.getDaoFromRq(rq)
                .flatMap(this::save)
                .flatMap(PlanMapper::getCreateRsFromDao);
    }

    /**
     * get a plan by id.
     *
     * @param id plan identifier.
     * @return {@link Optional} of {@link GetPlanRsDto}.
     */
    @Override
    public Optional<GetPlanRsDto> getById(final UUID id) {
        log.info("[getById]: ready to get the plan by id: {}", id);
        return getPlanById(id).flatMap(PlanMapper::getGetRsFromDao);
    }

    /**
     * get a plan by id.
     *
     * @param id plan identifier.
     * @return {@link Optional} of {@link PlanDao}.
     */
    private Optional<PlanDao> getPlanById(final UUID id) {
        Optional<PlanDao> result = Optional.empty();

        try {
            result = planRepo.findById(id);
            log.info("[getPlanById]: plan found ok by id: {}", id);
        } catch (Exception e) {
            log.error("[getPlanById]: error getting plan by id: {}, with error: {}", id, e.getMessage());
        }
        return result;
    }

    /**
     * get a plan by name.
     *
     * @param name plan name.
     * @return {@link Optional} of {@link GetPlanRsDto}.
     */
    @Override
    public Optional<GetPlanRsDto> getByName(final String name) {
        log.info("[getById]: ready to get the plan by name: {}", name);
        return getPlanByName(name).flatMap(PlanMapper::getGetRsFromDao);
    }

    /**
     * get a plan by name.
     *
     * @param name plan name.
     * @return {@link Optional} of {@link PlanDao}.
     */
    private Optional<PlanDao> getPlanByName(final String name) {
        Optional<PlanDao> result = Optional.empty();
        try {
            result = planRepo.findByName(name);
            log.info("[getPlanByName]: plan found ok by name: {}", name);
        } catch (Exception e) {
            log.error("[getPlanByName]: error getting plan by name: {}, with error: {}", name, e.getMessage());
        }
        return result;
    }

    /**
     * get all plans.
     *
     * @return {@link Optional} of {@link List} of {@link GetPlanRsDto}.
     */
    @Override
    public Optional<List<GetPlanRsDto>> getAll() {
        log.info("[getAll]: ready to get all plans.");
        return getAllPlan().flatMap(PlanMapper::getGetListRsFromListDao);
    }

    /**
     * get all plans.
     *
     * @return {@link Optional} of {@link List} of {@link PlanDao}.
     */
    public Optional<List<PlanDao>> getAllPlan() {
        Optional<List<PlanDao>> result = Optional.empty();

        try {
            final List<PlanDao> plans = planRepo.findAll();
            result = !plans.isEmpty() ? Optional.of(plans) : Optional.empty();
            log.info("[getAllPlan]: get plans ok:{}", plans.size());
        } catch (Exception e) {
            log.error("[getAllPlan]: error getting all plans: {}", e.getMessage());
        }
        return result;
    }

    /**
     * update a plan.
     *
     * @param rq to update a plan.
     * @param id plan identifier.
     * @return {@link Optional} of {@link UpdatePlanRsDto}.
     */
    @Override
    public Optional<UpdatePlanRsDto> update(final UUID id, final UpdatePlanRqDto rq, final String adminCode) {
        ServiceValidator.validateUpdatePlan(id, rq, adminCode);
        return updatePlan(id, rq)
                .map(PlanMapper::getUpdateRsFromDao)
                .orElseGet(() -> {
                    log.warn("[update]: error updating the plan: {}, not found.", id);
                    throw NotFoundException.builder()
                            .message("error updating the plan: %s, not found.".formatted(id))
                            .endpoint("/plan/{id}")
                            .build();
                });
    }

    /**
     * update the plan.
     * @param id plan identifier.
     * @param rq payload with the data to update.
     * @return {@link Optional} of {@link PlanDao}.
     */
    private Optional<PlanDao> updatePlan(final UUID id, final UpdatePlanRqDto rq) {
        return getPlanById(id)
                .map(dao -> updatePriceMonth(dao, rq))
                .map(dao -> updatePriceYear(dao, rq))
                .flatMap(this::save);
    }

    /**
     * update the dao with the data from request.
     * @param dao to update.
     * @param rq date to update.
     * @return {@link PlanDao}
     */
    private PlanDao updatePriceMonth(final PlanDao dao, final UpdatePlanRqDto rq) {
        final BigDecimal newPriceMonth = Validator.isValidBigDecimal(rq.priceMonth()) ?
                rq.priceMonth() : dao.getPriceMonth();
        return PlanDao.builder()
                .id(dao.getId())
                .updated(Validator.getNow())
                .name(dao.getName())
                .refreshByCam(dao.getRefreshByCam())
                .camsByMonth(dao.getCamsByMonth())
                .priceMonth(newPriceMonth)
                .priceYear(dao.getPriceYear())
                .created(dao.getCreated())
                .active(dao.getActive())
                .build();
    }

    /**
     * update the dao with the data from request.
     * @param dao to update.
     * @param rq date to update.
     * @return {@link PlanDao}
     */
    private PlanDao updatePriceYear(final PlanDao dao, final UpdatePlanRqDto rq) {
        final BigDecimal newPriceYear = Validator.isValidBigDecimal(rq.priceYear()) ?
                rq.priceYear() : dao.getPriceYear();
        return PlanDao.builder()
                .id(dao.getId())
                .updated(Validator.getNow())
                .name(dao.getName())
                .refreshByCam(dao.getRefreshByCam())
                .camsByMonth(dao.getCamsByMonth())
                .priceMonth(dao.getPriceMonth())
                .priceYear(newPriceYear)
                .created(dao.getCreated())
                .active(dao.getActive())
                .build();
    }

    /**
     * delete a plan by id.
     *
     * @param id plan identifier.
     * @return {@link Optional} of {@link DefaultRsDto}.
     */
    @Override
    public Optional<DefaultRsDto> deleteById(final UUID id, final String adminCode) {
        return Optional.empty();
    }

    /**
     * delete a plan by name.
     *
     * @param name plan name.
     * @return {@link Optional} of {@link DefaultRsDto}.
     */
    @Override
    public Optional<DefaultRsDto> deleteByName(final String name, final String adminCode) {
        return Optional.empty();
    }

    /**
     * validate the creation plan data.
     * @param rq the payload to create a plan.
     * @param adminCode authorization to create a plan.
     */
    private void validateCreate(final CreatePlanRqDto rq, final String adminCode) {
        ServiceValidator.validateCreatePlan(rq, adminCode);
        validatePlanName(rq.name());
        //TODO: falta validar el codigo de admin.
        log.info("[validateCreate]: the plan data is ok.");
    }

    /**
     * validate the name if exist before to create a plan.
     * @param name to validate.
     */
    private void validatePlanName(final String name) {
        getByName(name).ifPresentOrElse(dao -> {
            log.warn("[validateCreate]: the plan already exists by name: {}", name);
            throw CreatedException.builder()
                    .endpoint("/plan/")
                    .message("the plan already exists by name: {}%s".formatted(name))
                    .build();
        }, () -> log.info("[validateCreate]: the plan is correct, and ready to be created."));
    }

    /**
     * save a plan.
     * @param dao as a plan for save.
     * @return {@link Optional} of {@link PlanDao}.
     */
    private Optional<PlanDao> save(final PlanDao dao) {
        Optional<PlanDao> result = Optional.empty();
        try {
            planRepo.save(dao);
            result = Optional.of(dao);
            log.info("[save]: plan saved ok.");
        }catch (Exception e) {
            log.error("[save]: error saving plan: {}", e.getMessage());
        }
        return result;
    }

}
