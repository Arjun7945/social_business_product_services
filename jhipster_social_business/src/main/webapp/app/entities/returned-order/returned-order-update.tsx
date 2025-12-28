import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCustomerOrders } from 'app/entities/customer-order/customer-order.reducer';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { ReturnProductStatus } from 'app/shared/model/enumerations/return-product-status.model';
import { createEntity, getEntity, reset, updateEntity } from './returned-order.reducer';

export const ReturnedOrderUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const customerOrders = useAppSelector(state => state.customerOrder.entities);
  const customers = useAppSelector(state => state.customer.entities);
  const returnedOrderEntity = useAppSelector(state => state.returnedOrder.entity);
  const loading = useAppSelector(state => state.returnedOrder.loading);
  const updating = useAppSelector(state => state.returnedOrder.updating);
  const updateSuccess = useAppSelector(state => state.returnedOrder.updateSuccess);
  const returnProductStatusValues = Object.keys(ReturnProductStatus);

  const handleClose = () => {
    navigate(`/returned-order${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCustomerOrders({}));
    dispatch(getCustomers({}));
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
    values.returnDate = convertDateTimeToServer(values.returnDate);
    if (values.refundAmount !== undefined && typeof values.refundAmount !== 'number') {
      values.refundAmount = Number(values.refundAmount);
    }

    const entity = {
      ...returnedOrderEntity,
      ...values,
      order: customerOrders.find(it => it.id.toString() === values.order?.toString()),
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
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
          returnDate: displayDefaultDateTime(),
        }
      : {
          productClaimStatus: 'PENDING_RECEIPT',
          ...returnedOrderEntity,
          returnDate: convertDateTimeFromServer(returnedOrderEntity.returnDate),
          order: returnedOrderEntity?.order?.id,
          customer: returnedOrderEntity?.customer?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="whatsappProductServiceProApp.returnedOrder.home.createOrEditLabel" data-cy="ReturnedOrderCreateUpdateHeading">
            <Translate contentKey="whatsappProductServiceProApp.returnedOrder.home.createOrEditLabel">
              Create or edit a ReturnedOrder
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
                  id="returned-order-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrder.returnDate')}
                id="returned-order-returnDate"
                name="returnDate"
                data-cy="returnDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrder.paymentReceivedMode')}
                id="returned-order-paymentReceivedMode"
                name="paymentReceivedMode"
                data-cy="paymentReceivedMode"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrder.paymentReturnedMode')}
                id="returned-order-paymentReturnedMode"
                name="paymentReturnedMode"
                data-cy="paymentReturnedMode"
                type="text"
              />
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrder.productClaimStatus')}
                id="returned-order-productClaimStatus"
                name="productClaimStatus"
                data-cy="productClaimStatus"
                type="select"
              >
                {returnProductStatusValues.map(returnProductStatus => (
                  <option value={returnProductStatus} key={returnProductStatus}>
                    {translate(`whatsappProductServiceProApp.ReturnProductStatus.${returnProductStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('whatsappProductServiceProApp.returnedOrder.refundAmount')}
                id="returned-order-refundAmount"
                name="refundAmount"
                data-cy="refundAmount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="returned-order-order"
                name="order"
                data-cy="order"
                label={translate('whatsappProductServiceProApp.returnedOrder.order')}
                type="select"
                required
              >
                <option value="" key="0" />
                {customerOrders
                  ? customerOrders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="returned-order-customer"
                name="customer"
                data-cy="customer"
                label={translate('whatsappProductServiceProApp.returnedOrder.customer')}
                type="select"
                required
              >
                <option value="" key="0" />
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/returned-order" replace color="info">
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

export default ReturnedOrderUpdate;
