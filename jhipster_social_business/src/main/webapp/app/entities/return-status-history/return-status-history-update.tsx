import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getReturnedOrders } from 'app/entities/returned-order/returned-order.reducer';
import { ReturnStatus } from 'app/shared/model/enumerations/return-status.model';
import { createEntity, getEntity, reset, updateEntity } from './return-status-history.reducer';

export const ReturnStatusHistoryUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const returnedOrders = useAppSelector(state => state.returnedOrder.entities);
  const returnStatusHistoryEntity = useAppSelector(state => state.returnStatusHistory.entity);
  const loading = useAppSelector(state => state.returnStatusHistory.loading);
  const updating = useAppSelector(state => state.returnStatusHistory.updating);
  const updateSuccess = useAppSelector(state => state.returnStatusHistory.updateSuccess);
  const returnStatusValues = Object.keys(ReturnStatus);

  const handleClose = () => {
    navigate(`/return-status-history${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getReturnedOrders({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.changeTime = convertDateTimeToServer(values.changeTime);

    const entity = {
      ...returnStatusHistoryEntity,
      ...values,
      returnedOrder: returnedOrders.find(it => it.id.toString() === values.returnedOrder?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          changeTime: displayDefaultDateTime(),
        }
      : {
          status: 'RETURN_REQUESTED',
          ...returnStatusHistoryEntity,
          changeTime: convertDateTimeFromServer(returnStatusHistoryEntity.changeTime),
          returnedOrder: returnStatusHistoryEntity?.returnedOrder?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.returnStatusHistory.home.createOrEditLabel" data-cy="ReturnStatusHistoryCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.returnStatusHistory.home.createOrEditLabel">
              Create or edit a ReturnStatusHistory
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="return-status-history-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnStatusHistory.status')}
                id="return-status-history-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {returnStatusValues.map(returnStatus => (
                  <option value={returnStatus} key={returnStatus}>
                    {translate(`whatsappProductServiceProApp.ReturnStatus.${returnStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnStatusHistory.changeTime')}
                id="return-status-history-changeTime"
                name="changeTime"
                data-cy="changeTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="return-status-history-returnedOrder"
                name="returnedOrder"
                data-cy="returnedOrder"
                label={translate('whatsappProductServiceProApp.returnStatusHistory.returnedOrder')}
                type="select"
              >
                <option value="" key="0" />
                {returnedOrders
                  ? returnedOrders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/return-status-history" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ReturnStatusHistoryUpdate;
