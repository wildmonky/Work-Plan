import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_1e5a9fd227de640a1dbc8518f1b7eeb9
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('../app/flowform.html?flow_code=YYSQ&data_id="+mapRow.get("ID")+"');";return url;

    }
}
