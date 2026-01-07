import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './customer-order.reducer';

export const CustomerOrder = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const customerOrderList = useAppSelector(state => state.customerOrder.entities);
  const loading = useAppSelector(state => state.customerOrder.loading);
  const totalItems = useAppSelector(state => state.customerOrder.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="customer-order-heading" data-cy="CustomerOrderHeading">
        <Translate contentKey="whatsappProductServiceProApp.customerOrder.home.title">Customer Orders</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="whatsappProductServiceProApp.customerOrder.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/customer-order/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="whatsappProductServiceProApp.customerOrder.home.createLabel">Create new Customer Order</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {customerOrderList && customerOrderList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('orderTime')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.orderTime">Order Time</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('orderTime')} />
                </th>
                <th className="hand" onClick={sort('totalAmount')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.totalAmount">Total Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalAmount')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('paymentMethod')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.paymentMethod">Payment Method</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('paymentMethod')} />
                </th>
                <th className="hand" onClick={sort('confirmedAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.confirmedAt">Confirmed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('confirmedAt')} />
                </th>
                <th className="hand" onClick={sort('removedCustomerId')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.removedCustomerId">Removed Customer Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('removedCustomerId')} />
                </th>
                <th className="hand" onClick={sort('removedDeliveryPersonId')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.removedDeliveryPersonId">
                    Removed Delivery Person Id
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('removedDeliveryPersonId')} />
                </th>
                <th className="hand" onClick={sort('transactionId')}>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.transactionId">Transaction Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('transactionId')} />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.history">History</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.customer">Customer</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.customerOrder.deliveryPerson">Delivery Person</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {customerOrderList.map((customerOrder, i) => (
                <tr key={customerOrder.id} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/customer-order/${customerOrder.id}`} color="link" size="sm">
                      {customerOrder.id}
                    </Button>
                  </td>
                  <td>
                    {customerOrder.orderTime ? <TextFormat type="date" value={customerOrder.orderTime} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{customerOrder.totalAmount}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.OrderStatus.${customerOrder.status}`} />
                  </td>
                  <td>{customerOrder.paymentMethod}</td>
                  <td>
                    {customerOrder.confirmedAt ? (
                      <TextFormat type="date" value={customerOrder.confirmedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {customerOrder.removedCustomerId ? (
                      <Link to={`/removed-user/${customerOrder.removedCustomerId}`}>{customerOrder.removedCustomerId}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {customerOrder.removedDeliveryPersonId ? (
                      <Link to={`/removed-user/${customerOrder.removedDeliveryPersonId}`}>{customerOrder.removedDeliveryPersonId}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{customerOrder.transactionId}</td>
                  <td>
                    {customerOrder.history ? (
                      <Link to={`/order-status-history/${customerOrder.history.id}`}>{customerOrder.history.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {customerOrder.customer ? <Link to={`/customer/${customerOrder.customer.id}`}>{customerOrder.customer.name}</Link> : ''}
                  </td>
                  <td>
                    {customerOrder.deliveryPerson ? (
                      <Link to={`/delivery-person/${customerOrder.deliveryPerson.id}`}>{customerOrder.deliveryPerson.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/customer-order/${customerOrder.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/customer-order/${customerOrder.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/customer-order/${customerOrder.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="whatsappProductServiceProApp.customerOrder.home.notFound">No Customer Orders found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={customerOrderList && customerOrderList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default CustomerOrder;
