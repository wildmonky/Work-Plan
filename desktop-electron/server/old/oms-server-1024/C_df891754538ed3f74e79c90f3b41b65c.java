import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_df891754538ed3f74e79c90f3b41b65c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('module/bussines/zwdd/fhdl_modify_log.html?fhdl_id=="+mapRow.get("FHDL_ID")+"');";return url;

    }
}
